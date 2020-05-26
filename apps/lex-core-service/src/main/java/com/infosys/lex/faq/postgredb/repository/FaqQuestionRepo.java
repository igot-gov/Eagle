/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.postgredb.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.faq.postgredb.entity.FaqQuestion;
import com.infosys.lex.faq.postgredb.entity.FaqQuestionPrimaryKey;
import com.infosys.lex.faq.postgredb.projection.FaqQuestionProjection;

@Repository
public interface FaqQuestionRepo extends JpaRepository<FaqQuestion, FaqQuestionPrimaryKey> {

	@Query("select p.faqQuestionPrimaryKey.groupId as groupId,p.faqQuestionPrimaryKey.questionSeq as questionSeq,p.question as question,p.answer as answer from FaqQuestion p where p.faqQuestionPrimaryKey.rootOrg =?1 and p.faqQuestionPrimaryKey.language =?2 and p.faqQuestionPrimaryKey.groupId =?3 order by p.faqQuestionPrimaryKey.questionSeq ")
	List<FaqQuestionProjection> findQuestion(String rootOrg, String language, String groupId);

	@Modifying
	@Query(value = "delete from FaqQuestion p where p.faqQuestionPrimaryKey.rootOrg =?1 and p.faqQuestionPrimaryKey.language =?2 and p.faqQuestionPrimaryKey.groupId=?3")
	void deleteQuestion(String rootOrg, String language, String groupId);

	@Query(nativeQuery = true, value = "select group_id,question_seq,question,answer from faq_question where root_org=?1 and language=?2 and search_tokens @@ plainto_tsquery(cast(?3 as regconfig), ?4)")
	List<Map<String, Object>> searchText(String rootOrg, String langCode, String queryParam, String query);

	@Query(nativeQuery = true, value = "select group_id,question_seq,question,answer from faq_question where root_org=?1 and language=?2 order by question_seq")
	List<Map<String, Object>> getAllQuestions(String rootOrg, String langCode);

}
