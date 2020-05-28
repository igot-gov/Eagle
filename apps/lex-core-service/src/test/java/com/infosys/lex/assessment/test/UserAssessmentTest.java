/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
//substitute url based on requirement
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//
//import org.apache.cassandra.exceptions.ConfigurationException;
//import org.apache.thrift.transport.TTransportException;
//import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.datastax.driver.core.Cluster;
//import com.datastax.driver.core.Session;
//import com.datastax.driver.core.utils.UUIDs;
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles(profiles = "AssessmentProfile")
//@ContextConfiguration(classes = { DaoConfiguration.class })
//public class UserAssessmentTest {
//
//	public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS bodhi WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";
//
//	public static final String KEYSPACE_ACTIVATE_QUERY = "USE bodhi;";
//
//	public static final String KEYSPACE = "bodhi";
//
//	public static final String Assessment_master = "user_assessment_master";
//
//	@Autowired
//	private UserAssessmentMasterRepository userAssessmentMasterRepository;
//
//	@BeforeClass
//	public static void startCassandraEmbedded()
//			throws InterruptedException, TTransportException, ConfigurationException, IOException {
//		EmbeddedCassandraServerHelper.startEmbeddedCassandra(100000L);
//		final Cluster cluster = Cluster.builder().addContactPoints("IPaddress").withPort(9142).build();
//		final Session session = cluster.connect();
//		session.execute(KEYSPACE_CREATION_QUERY);
//		session.execute(KEYSPACE_ACTIVATE_QUERY);
//		Thread.sleep(5000);
//	}
//
//	@Test
//	public void test() {
//		UserAssessmentMasterModel userAssessment = new UserAssessmentMasterModel(
//				new UserAssessmentMasterPrimaryKeyModel(new Date(), "parent", new BigDecimal(100), UUIDs.timeBased()),
//				0, new Date(), 0, 0, "Course", new BigDecimal(60), "source", "Title", "user");
//
//		userAssessment = userAssessmentMasterRepository.save(userAssessment);
//		assertThat(userAssessment.getSourceTitle(), is("Title"));
//	}
//
//}
