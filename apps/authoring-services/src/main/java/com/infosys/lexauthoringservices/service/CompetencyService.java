package com.infosys.lexauthoringservices.service;

import com.infosys.lexauthoringservices.model.Competency;

public interface CompetencyService {

	public void updateCompetencyData(Competency competency);

	public Competency processUpdateCompetencyData(Competency competency);
}
