package org.concord.otrunk.overlay;

import org.concord.otrunk.user.OTUserObject;

/**
 * This is a real object which mirrors OTUserSubmission -- it's an easier to create alternative for on-the-fly
 * user submission information tracking.
 * @author aunger
 *
 */
public class UserSubmissionImpl implements UserSubmission {
    public static final int MOST_RECENT_SUBMISSION = -1;
    
    // Add some fake submission numbers, used to signal when to display something special
    public static final int EXAMPLE_SUBMISSION = 1; // using 1 for backward compatibility
    public static final int CONDENSED_EXAMPLE_SUBMISSION = 2; // using positive numbers so that StudentWorkPanel thinks something has been submitted
    
    private OTUserObject user;
    private int submissionNumber = MOST_RECENT_SUBMISSION;
    
    public UserSubmissionImpl(OTUserObject user) {
        this.user = user;
    }
    
    public UserSubmissionImpl(OTUserObject user, int submissionNumber) {
        this(user);
        this.submissionNumber = submissionNumber;
    }

    public OTUserObject getUser() {
        return user;
    }

    public void setUser(OTUserObject user) {
        this.user = user;
    }

    public int getSubmissionNumber() {
        return submissionNumber;
    }

    public void setSubmissionNumber(int number) {
        this.submissionNumber = number;
    }
}