package org.concord.otrunk.view.document.edit;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JEditorPane;

public class MyJEditorPane extends JEditorPane
{
	public void paste() {
        Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        if (content != null) {
            DataFlavor[] flavors = content.getTransferDataFlavors();
            try {
                for (int i = 0; i < flavors.length; i++) {
                    if (String.class.equals(flavors[i].getRepresentationClass())) {
                        Object data = content.getTransferData(flavors[i]);

                        if (flavors[i].isMimeTypeEqual("text/plain")) {
                            // This works but we lose all the formatting.
                            replaceSelection(data.toString());
                            break;
                        } 
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
