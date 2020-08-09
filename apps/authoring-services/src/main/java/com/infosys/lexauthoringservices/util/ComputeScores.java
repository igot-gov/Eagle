/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.CriteriaModel;
import com.infosys.lexauthoringservices.model.EvaluatorModel;
import com.infosys.lexauthoringservices.model.QualifierModel;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.EvaluationCriteria;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreCriteriaRepository;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreQualifier;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreQualifierRepository;
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
    private static final String IDENTIFIER_PREFIX = "lex_score_";

    private ScoreCriteriaRepository scoreCriteriaRepository;
    private ScoreQualifierRepository scoreQualifierRepository;

    public ComputeScores(ScoreCriteriaRepository scoreCriteriaRepository, ScoreQualifierRepository scoreQualifierRepository){

        this.scoreCriteriaRepository = scoreCriteriaRepository;
        this.scoreQualifierRepository = scoreQualifierRepository;
    }

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
        evaluatorModel.setFinalMaxScore(finalTotatScore);

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

    }

}
