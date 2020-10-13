package com.infosys.service;

import java.io.IOException;
import java.util.List;

public interface CompetencySearchService {
    /**
     * Get the list of identifier which contains the competency id's
     * @param rootOrg
     * @param competencyId
     * @return List of identifiers
     * @throws IOException
     */
    public List<Object> getLexIdsContainingCompId(String rootOrg, String competencyId) throws IOException;
}
