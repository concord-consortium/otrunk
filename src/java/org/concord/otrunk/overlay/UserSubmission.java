package org.concord.otrunk.overlay;

import org.concord.otrunk.user.OTUserObject;

/**
 * This interface is a mirror of OTUserSubmission. When updating methods on this interface,
 * be sure to update OTUserSubmission to match!
 * @author aunger
 *
 */
public interface UserSubmission {
    public static final int MOST_RECENT_SUBMISSION = -1;
    
    public OTUserObject getUser();
    public void setUser(OTUserObject user);

    public int getSubmissionNumber();
    public void setSubmissionNumber(int number);
}
