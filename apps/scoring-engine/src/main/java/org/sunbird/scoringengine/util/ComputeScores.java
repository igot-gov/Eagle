
package org.sunbird.scoringengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.sunbird.scoringengine.models.CriteriaModel;
import org.sunbird.scoringengine.models.EvaluatorModel;
import org.sunbird.scoringengine.models.QualifierModel;
import org.sunbird.scoringengine.repository.cassandra.bodhi.*;
import org.sunbird.scoringengine.schema.model.Criteria;
import org.sunbird.scoringengine.schema.model.Qualifier;
import org.sunbird.scoringengine.schema.model.Range;
import org.sunbird.scoringengine.schema.model.ScoringTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ComputeScores {

    private Logger logger = LoggerFactory.getLogger(ComputeScores.class);
    private ObjectMapper mapper = new ObjectMapper();

    private static SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String identifierPrefix = "lex_score_";

    private ScoreCriteriaRepository scoreCriteriaRepository;

    private ScoreQualifierRepository scoreQualifierRepository;
    private ScoringTemplate scoringTemplate;

    private static final String  MIN_SCORE_PASS_CONST = "pass";

    private static final String  MIN_SCORE_FAIL_CONST = "fail";

    public ComputeScores(ScoreCriteriaRepository scoreCriteriaRepository, ScoreQualifierRepository scoreQualifierRepository){

        this.scoreCriteriaRepository = scoreCriteriaRepository;
        this.scoreQualifierRepository = scoreQualifierRepository;
    }

    public ComputeScores(ScoringTemplate scoringTemplate){
        this.scoringTemplate = scoringTemplate;
    }

    /**
     * Compute using scoring schema and with fixed and range type
     * @param evaluatorModel
     * @throws Exception
     */
    public void computeV2(EvaluatorModel evaluatorModel) throws Exception {

        for (CriteriaModel cm : evaluatorModel.getCriteriaModels()) {

            //to get maxscore , minacceptablescore
            Map<String, Criteria> criteriaMap = scoringTemplate.getCriteria().stream().collect(Collectors.toMap(c -> c.getCriteria(), c -> c));
            Criteria criteria = criteriaMap.get(cm.getCriteria());
            evaluatorModel.setTemplateId(scoringTemplate.getTemplate_id());
            evaluatorModel.setTempleteName(scoringTemplate.getTemplateName());
            evaluatorModel.setRootOrg(scoringTemplate.getRootOrg());
            evaluatorModel.setOrg(scoringTemplate.getOrg());
            //EvaluationCriteria criteria = scoreCriteriaRepository.findCriteriaByName(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), cm.getCriteria());

            logger.info("EvaluationCriteria: ", mapper.writeValueAsString(criteria));
            double maxScore = criteria.getMax_score();
            cm.setMaxScore(maxScore);
            double minScore = criteria.getMin_acceptable_score();
            cm.setMinScore(minScore);
            double weightage = criteria.getWeightage();
            cm.setWeightage(weightage);


            Map<String, Qualifier> qualifierMap = criteria.getQualifiers().stream().collect(Collectors.toMap(Qualifier::getQualifier, qualifier -> qualifier));

            Map<String, Map<String, Integer>> qualifierFixedScores = criteria.getQualifiers().stream().collect(Collectors.toMap(Qualifier::getQualifier, Qualifier::getFixed_score));
//            List<ScoreQualifier> scoreQualifiers = scoreQualifierRepository.findQualifiersByCriteria(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), criteria.getCriteriaId());
//            logger.info("scoreQualifiers: ",mapper.writeValueAsString(scoreQualifiers));
//            Map<String, Map<String, Integer>> qualifierFixedScores = scoreQualifiers.stream().collect(
//                    Collectors.toMap(ScoreQualifier::getQualifier, ScoreQualifier::getFixedScore));
            Map<String, Map<String, Range>> qualifierRangeScores = criteria.getQualifiers().stream().collect(Collectors.toMap(Qualifier::getQualifier, Qualifier::getRange_score));
            int maxScoreExcludedValue = 0;
            for (QualifierModel qm : cm.getQualifiers()) {
                if (qm.getEvaluated() != null) {
                    int score = qualifierFixedScores.get(qm.getName()).get(qm.getEvaluated());
                    qm.setScoreValue(score);
                    qm.setScoringType("fixed");
                }
                else {
                    Range range = qualifierRangeScores.get(qm.getName()).get(qm.getScoreRange());
                    qm.setScoreValue(range.getAssignedValue());
                    qm.setScoringType("ranged");

                }

                if (Boolean.TRUE == qualifierMap.get(qm.getName()).getModify_max_score() &&
                        qualifierMap.get(qm.getName()).getMax_score_modify_value().containsKey(qm.getEvaluated())) {
                    maxScoreExcludedValue += qualifierMap.get(qm.getName()).getMax_score_modify_value().get(qm.getEvaluated());
                }
                qm.setDescription(qualifierMap.get(qm.getName()).getDescription());
            }
            cm.setMaxScore(cm.getMaxScore() - maxScoreExcludedValue);
            if (Boolean.TRUE == criteria.getMin_score_weightage_enable()) {
                cm.setMinScore(cm.getMaxScore() * criteria.getMin_score_weightage());
            }
            List<Double> scoreVals = cm.getQualifiers().stream().map(q -> q.getScoreValue()).collect(Collectors.toList());
            cm.setTotalScore(MathFunction.sum(scoreVals));
            cm.setWeightedAvg(MathFunction.weightedAvg(scoreVals, weightage));
            cm.setMaxWeightedAvg(MathFunction.maxWeightedAvg(cm.getMaxScore(), weightage));
            cm.setMinWeightedAvg(MathFunction.minWeightedAvg(minScore, weightage));
            cm.setWeightedScore(MathFunction.weightedScore(cm.getTotalScore(), cm.getMaxScore(), weightage));
        }

        List<Double> criteriaScoreValues = evaluatorModel.getCriteriaModels().stream().map(c -> c.getTotalScore()).collect(Collectors.toList());
        evaluatorModel.setFinalTotalScore(MathFunction.sum(criteriaScoreValues));

        List<Double> criteriaWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalWeightedAvg(MathFunction.sum(criteriaWeightedAvgVals));


        List<Double> criteriaMaxScoreVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMaxScore).collect(Collectors.toList());
        evaluatorModel.setFinalMaxScore(MathFunction.sum(criteriaMaxScoreVals));

        List<Double> criteriaMaxWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMaxWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalMaxWeightedAvg(MathFunction.sum(criteriaMaxWeightedAvgVals));

        List<Double> criteriaMinScoreVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMinScore).collect(Collectors.toList());
        evaluatorModel.setFinalMinScore(MathFunction.sum(criteriaMinScoreVals));

        List<Double> criteriaMinWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMinWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalMinWeightedAvg(MathFunction.sum(criteriaMinWeightedAvgVals));

        List<Double> criteriaWeightsVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getWeightedScore).collect(Collectors.toList());
        evaluatorModel.setFinalWeightedScore(MathFunction.sum(criteriaWeightsVals));

        setScoringStatus(evaluatorModel, scoringTemplate);

        String timeStamp = formatterDateTime.format(Calendar.getInstance().getTime());
        evaluatorModel.setTimeStamp(timeStamp);

        long millsec = System.currentTimeMillis();
        evaluatorModel.setIdentifier(identifierPrefix + millsec);

    }

    /**
     * Compute using scoring tables with fixed score type
     * @param evaluatorModel
     * @throws Exception
     */
    public void compute(EvaluatorModel evaluatorModel) throws Exception{

        for (CriteriaModel cm : evaluatorModel.getCriteriaModels()){

            //to get maxscore , minacceptablescore
            EvaluationCriteria criteria = scoreCriteriaRepository.findCriteriaByName(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), cm.getCriteria());

            logger.info("EvaluationCriteria: ",mapper.writeValueAsString(criteria));
            double maxScore = criteria.getMaxScore();
            cm.setMaxScore(maxScore);
            double minScore = criteria.getMinScore();
            cm.setMinScore(minScore);
            double weightage = criteria.getWeightage();
            cm.setWeightage(weightage);


            List<ScoreQualifier> scoreQualifiers = scoreQualifierRepository.findQualifiersByCriteria(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), criteria.getCriteriaId());
            logger.info("scoreQualifiers: ",mapper.writeValueAsString(scoreQualifiers));
            Map<String, Map<String, Integer>> qualifierFixedScores = scoreQualifiers.stream().collect(
                    Collectors.toMap(ScoreQualifier::getQualifier, ScoreQualifier::getFixedScore));

            for(QualifierModel qm : cm.getQualifiers()){
                int score = qualifierFixedScores.get(qm.getName()).get(qm.getEvaluated());
                qm.setScoreValue(score);
                qm.setScoringType("fixed");
            }

            List<Double> scoreVals = cm.getQualifiers().stream().map(q -> q.getScoreValue()).collect(Collectors.toList());
            cm.setTotalScore(MathFunction.sum(scoreVals));
            cm.setWeightedAvg(MathFunction.weightedAvg(scoreVals ,weightage));
            cm.setMaxWeightedAvg(MathFunction.maxWeightedAvg(maxScore, weightage));
            cm.setMinWeightedAvg(MathFunction.minWeightedAvg(minScore, weightage));

        }

        List<Double> criteriaScoreValues = evaluatorModel.getCriteriaModels().stream().map(c -> c.getTotalScore()).collect(Collectors.toList());
        evaluatorModel.setFinalTotalScore(MathFunction.sum(criteriaScoreValues));

        List<Double> criteriaWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalWeightedAvg(MathFunction.sum(criteriaWeightedAvgVals));


        List<Double> criteriaMaxScoreVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMaxScore).collect(Collectors.toList());
        evaluatorModel.setFinalMaxScore(MathFunction.sum(criteriaMaxScoreVals));

        List<Double> criteriaMaxWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMaxWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalMaxWeightedAvg(MathFunction.sum(criteriaMaxWeightedAvgVals));

        List<Double> criteriaMinScoreVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMinScore).collect(Collectors.toList());
        evaluatorModel.setFinalMinScore(MathFunction.sum(criteriaMinScoreVals));

        List<Double> criteriaMinWeightedAvgVals = evaluatorModel.getCriteriaModels().stream().map(CriteriaModel::getMinWeightedAvg).collect(Collectors.toList());
        evaluatorModel.setFinalMinWeightedAvg(MathFunction.sum(criteriaMinWeightedAvgVals));


        String timeStamp = formatterDateTime.format(Calendar.getInstance().getTime());
        evaluatorModel.setTimeStamp(timeStamp);

        long millsec = System.currentTimeMillis();
        evaluatorModel.setIdentifier(identifierPrefix + millsec);

    }

  /*  public void compute(EvaluatorModel evaluatorModel) throws Exception{

        for (CriteriaModel cm : evaluatorModel.getCriteriaModels()){

            //to get maxscore , minacceptablescore
            EvaluationCriteria criteria = scoreCriteriaRepository.findCriteriaByName(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), cm.getCriteria());

            logger.info("EvaluationCriteria: ",mapper.writeValueAsString(criteria));
            double maxScore = criteria.getMaxScore();
            cm.setMaxScore(maxScore);
            double minScore = criteria.getMinScore();
            cm.setMinScore(minScore);
            double weightage = criteria.getWeightage();
            cm.setWeightage(weightage);


            List<ScoreQualifier> scoreQualifiers = scoreQualifierRepository.findQualifiersByCriteria(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), criteria.getCriteriaId());
            logger.info("scoreQualifiers: ",mapper.writeValueAsString(scoreQualifiers));
            Map<String, Map<String, Integer>> qualifierFixedScores = scoreQualifiers.stream().collect(
                    Collectors.toMap(ScoreQualifier::getQualifier, ScoreQualifier::getFixedScore));

            for(QualifierModel qm : cm.getQualifiers()){
                int score = qualifierFixedScores.get(qm.getName()).get(qm.getEvaluated());
                qm.setScoreValue(score);
                qm.setScoringType("fixed");
            }

            double totalScore = cm.getQualifiers().stream().mapToDouble(QualifierModel::getScoreValue).sum();
            cm.setTotalScore(totalScore);

            //computed: weitageAvg, maxweightageAvg, minWeightageAvg
            double weightedAvg = totalScore * weightage;
            double maxweightedAvg = maxScore * weightage;
            double minWeightedAvg = minScore * weightage;

            cm.setWeightedAvg(weightedAvg);
            cm.setMaxWeightedAvg(maxweightedAvg);
            cm.setMinWeightedAvg(minWeightedAvg);

        }

        double finalTotatScore = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getTotalScore).sum();
        evaluatorModel.setFinalTotalScore(finalTotatScore);

        double finalWeightedAvg = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getWeightedAvg).sum();
        evaluatorModel.setFinalWeightedAvg(finalWeightedAvg);

        double finalMaxScore = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getMaxScore).sum();
        evaluatorModel.setFinalMaxScore(finalMaxScore);

        double finalMaxWeightedAvg = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getMaxWeightedAvg).sum();
        evaluatorModel.setFinalMaxWeightedAvg(finalMaxWeightedAvg);

        double finalMinScore = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getMinScore).sum();
        evaluatorModel.setFinalMinScore(finalMinScore);

        double finalMinWeightedAvg = evaluatorModel.getCriteriaModels().stream().mapToDouble(CriteriaModel::getMinWeightedAvg).sum();
        evaluatorModel.setFinalMinWeightedAvg(finalMinWeightedAvg);


        String timeStamp = formatterDateTime.format(Calendar.getInstance().getTime());
        evaluatorModel.setTimeStamp(timeStamp);

        long millsec = System.currentTimeMillis();
        evaluatorModel.setIdentifier(IDENTIFIER_PREFIX + millsec);

    }*/

    /**
     *
     * @param evaluatorModel Evaluator Model
     * @param scoringTemplate Scoring Template
     */
    private void setScoringStatus(EvaluatorModel evaluatorModel, ScoringTemplate scoringTemplate) {
        try {
            boolean isMinimumCriteriaPassed = true;
            for (CriteriaModel criteriaModel : evaluatorModel.getCriteriaModels()) {
                if (criteriaModel.getTotalScore() < criteriaModel.getMinScore()) {
                    isMinimumCriteriaPassed = false;
                    criteriaModel.setQualifiedMinCriteria(false);
                    evaluatorModel.setQualifiedMinCriteria(false);
                    if (!scoringTemplate.getStatus_on_min_criteria().isEmpty())
                        evaluatorModel.setScoreGrade(scoringTemplate.getStatus_on_min_criteria().getOrDefault(MIN_SCORE_FAIL_CONST, ""));
                }
            }
            if (isMinimumCriteriaPassed && !scoringTemplate.getScore_grades().isEmpty()) {
                for (HashMap<String, String> hm : scoringTemplate.getScore_grades()) {
                    Map.Entry<String, String> entry = hm.entrySet().iterator().next();
                    double min = Double.parseDouble(entry.getKey().split("-")[0]);
                    double max = Double.parseDouble(entry.getKey().split("-")[1]);
                    if (min < evaluatorModel.getFinalWeightedScore() && max > evaluatorModel.getFinalWeightedScore()) {
                        evaluatorModel.setScoreGrade(entry.getValue());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error occurred while setting the score status!");
            logger.error(ex.toString());
        }
    }

}
