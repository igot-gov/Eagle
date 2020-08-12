/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.infosys.scoringengine.models.CriteriaModel;
import com.infosys.scoringengine.models.EvaluatorModel;
import com.infosys.scoringengine.models.QualifierModel;
import com.infosys.scoringengine.repository.cassandra.bodhi.*;
import com.infosys.scoringengine.schema.model.Criteria;
import com.infosys.scoringengine.schema.model.Qualifier;
import com.infosys.scoringengine.schema.model.Range;
import com.infosys.scoringengine.schema.model.ScoringTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public void computeV2(EvaluatorModel evaluatorModel) throws Exception{

        for (CriteriaModel cm : evaluatorModel.getCriteriaModels()){

            //to get maxscore , minacceptablescore
            Map<String, Criteria> criteriaMap = scoringTemplate.getCriteria().stream().collect(Collectors.toMap(c->c.getCriteria(), c->c));
            Criteria criteria = criteriaMap.get(cm.getCriteria());
            evaluatorModel.setTemplateId(scoringTemplate.getTemplate_id());
            evaluatorModel.setTempleteName(scoringTemplate.getTemplateName());
            //EvaluationCriteria criteria = scoreCriteriaRepository.findCriteriaByName(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), cm.getCriteria());

            logger.info("EvaluationCriteria: ",mapper.writeValueAsString(criteria));
            double maxScore = criteria.getMax_score();
            cm.setMaxScore(maxScore);
            double minScore = criteria.getMin_acceptable_score();
            cm.setMinScore(minScore);
            double weightage = criteria.getWeightage();
            cm.setWeightage(weightage);


            Map<String, Map<String, Integer>> qualifierFixedScores = criteria.getQualifiers().stream().collect(Collectors.toMap(Qualifier::getQualifier, Qualifier::getFixed_score));
//            List<ScoreQualifier> scoreQualifiers = scoreQualifierRepository.findQualifiersByCriteria(evaluatorModel.getRootOrg(), evaluatorModel.getOrg(), criteria.getCriteriaId());
//            logger.info("scoreQualifiers: ",mapper.writeValueAsString(scoreQualifiers));
//            Map<String, Map<String, Integer>> qualifierFixedScores = scoreQualifiers.stream().collect(
//                    Collectors.toMap(ScoreQualifier::getQualifier, ScoreQualifier::getFixedScore));
            Map<String, Map<String, Range>> qualifierRangeScores = criteria.getQualifiers().stream().collect(Collectors.toMap(Qualifier::getQualifier, Qualifier::getRange_score));

            for(QualifierModel qm : cm.getQualifiers()){
                if(qm.getEvaluated()!=null){

                    int score = qualifierFixedScores.get(qm.getName()).get(qm.getEvaluated());
                    qm.setScoreValue(score);
                    qm.setScoringType("fixed");
                } else {
                    Range range = qualifierRangeScores.get(qm.getName()).get(qm.getScoreRange());
                    qm.setScoreValue(range.getAssignedValue());
                    qm.setScoringType("ranged");

                }

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


}
