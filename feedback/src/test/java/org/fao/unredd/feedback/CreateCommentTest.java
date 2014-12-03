package org.fao.unredd.feedback;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.fao.unredd.portal.PersistenceException;
import org.junit.Test;

public class CreateCommentTest {

	private static final String validGeometry = "POINT(0 0)";
	private static final String validComment = "boh";
	private static final String validEmail = "nombre@dominio.com";
	private static final String validSRID = "900913";
	private Feedback feedback;

	@Test
	public void testMissingParameters() throws CannotSendMailException,
			PersistenceException {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		testMandatoryParameter(null, validSRID, validComment, validEmail);
		testMandatoryParameter(validGeometry, null, validComment, validEmail);
		testMandatoryParameter(validGeometry, validSRID, null, validEmail);
		testMandatoryParameter(validGeometry, validSRID, validComment, null);
	}

	private void testMandatoryParameter(String geom, String srid,
			String comment, String email) throws CannotSendMailException,
			PersistenceException {
		try {
			feedback.insertNew(geom, srid, comment, email);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testInvalidInsertNoMail() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		doThrow(new PersistenceException("", null)).when(persistence)
				.insert(anyString(), anyString(), anyString(), anyString(),
						anyString());
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);

		try {
			feedback.insertNew(validGeometry, validSRID, validComment,
					validEmail);
			fail();
		} catch (Exception e) {
		}
		verify(mailer, never()).sendVerificationMail(anyString(), anyString());
	}

	@Test
	public void testInsert() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validSRID, validComment, validEmail);
		verify(persistence, times(1)).insert(eq(validGeometry), eq(validSRID),
				eq(validComment), eq(validEmail), anyString());
	}

	@Test
	public void testDifferentVerificationCodes() throws Exception {
		feedback = new Feedback(mock(FeedbackPersistence.class),
				mock(Mailer.class));
		assertTrue(feedback.insertNew(validGeometry, validSRID, validComment,
				validEmail) != feedback.insertNew("POINT(1 1)", validSRID,
				validComment, validEmail));
	}

	@Test
	public void testInsertCleansOutOfDate() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		feedback = new Feedback(persistence, mock(Mailer.class));
		feedback.insertNew(validGeometry, validSRID, validComment, validEmail);
		verify(persistence, times(1)).cleanOutOfDate();
	}

	@Test
	public void testVerifyComment() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.existsUnverified("100")).thenReturn(true);
		Mailer mailer = mock(Mailer.class);
		feedback = new Feedback(persistence, mailer);
		feedback.verify("100");
		verify(persistence).verify("100");
		verify(mailer).sendVerifiedMail("100");
	}

	@Test
	public void testVerifyVerifiedComment() throws Exception {
		FeedbackPersistence persistence = mock(FeedbackPersistence.class);
		when(persistence.existsUnverified("100")).thenReturn(false);
		feedback = new Feedback(persistence, mock(Mailer.class));
		try {
			feedback.verify("100");
			fail();
		} catch (VerificationCodeNotFoundException e) {
		}
	}
}
