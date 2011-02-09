package org.concord.otrunk.overlay;

import org.concord.framework.otrunk.OTObjectInterface;
import org.concord.otrunk.user.OTUserObject;

/**
 * This interface is a mirror of UserSubmission. When updating methods on this interface,
 * be sure to update UserSubmission to match!
 * @author aunger
 *
 */
public interface OTUserSubmission extends OTObjectInterface, UserSubmission {
    public OTUserObject getUser();
    public void setUser(OTUserObject user);

    public static final int DEFAULT_submissionNumber = UserSubmission.MOST_RECENT_SUBMISSION;
    public int getSubmissionNumber();
    public void setSubmissionNumber(int number);
}
