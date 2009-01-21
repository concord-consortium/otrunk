package org.concord.otrunk.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentViewContext;
import org.concord.framework.otrunk.view.OTViewContext;
import org.concord.swing.CustomDialog;

public class ObjectChooserUtil
{
	public static OTObject selectObjectFromViewContext(Component parent,
	    OTJComponentViewContext viewContext, Object[] omitObjectsAndClasses)
	{
		if (viewContext == null) {
			System.err.println("Error selecting object: OTJComponentViewContext is null");
			return null;
		}

		return showSelectionDialog(parent,
		    getObjectsFromContext(viewContext, omitObjectsAndClasses));

	}

	public static OTObject selectObjectFromSharedObjects(Component parent,
	    OTViewContext viewContext, Class<?> filter)
	{
		return showSelectionDialog(parent, getObjectFromSharedObjects(viewContext, filter));
	}

	public static OTObject selectObjectFromSharedObjects(Component parent,
	    OTSharingManager sharingManager, Class<?> filter)
	{

		return showSelectionDialog(parent, sharingManager.getAllSharedObjects(filter));
	}

	public static OTObject selectObjectFromContextAndShared(Component parent,
	    OTJComponentViewContext jViewContext, OTViewContext viewContext,
	    Object[] omitObjectsAndClasses)
	{
		Vector<OTObject> allObjects = getObjectsFromContext(jViewContext, omitObjectsAndClasses);
		Vector<OTObject> objectsFromShared = getObjectFromSharedObjects(viewContext, null);
		for (int i = 0; i < objectsFromShared.size(); i++) {
			allObjects.add(objectsFromShared.get(i));
		}
		return showSelectionDialog(parent, allObjects);
	}

	public static OTObject selectObject(Component parent, Vector<OTObject> objects)
	{
		return showSelectionDialog(parent, objects);
	}

	private static Vector<OTObject> getObjectsFromContext(OTJComponentViewContext viewContext,
	    Object[] omitObjectsAndClasses)
	{
		Object[] objects = viewContext.getAllObjects();
		final Vector<OTObject> cleanedObjects = new Vector<OTObject>();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] instanceof OTObject) {
				OTObject obj = (OTObject) objects[i];
				if (!contains(omitObjectsAndClasses, obj)) {
					cleanedObjects.add(obj);
				}
			}
		}
		return cleanedObjects;
	}

	private static Vector<OTObject> getObjectFromSharedObjects(OTViewContext viewContext, 
		Class<?> filter)
	{
		OTSharingManager sharingManager =
		    (OTSharingManager) viewContext.getViewService(OTSharingManager.class);
		if (sharingManager == null) {
			System.err.println("Cannot create sharing manager from " + viewContext);
			return null;
		}
		return sharingManager.getAllSharedObjects(filter);
	}

	private static OTObject showSelectionDialog(Component parent, Vector<OTObject> objects)
	{
		OTObjectTableModel otObjectTableModel = new OTObjectTableModel(objects);
		final JTable table = new JTable(otObjectTableModel);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setRowSelectionAllowed(true);
		table.setPreferredScrollableViewportSize(new Dimension(400, 100));
		JScrollPane scroll = new JScrollPane(table);

		JLabel instr = new JLabel("Select an object:");

		JPanel selectPanel = new JPanel(new BorderLayout());
		selectPanel.add(instr, BorderLayout.NORTH);
		selectPanel.add(scroll, BorderLayout.CENTER);

		int retValue =
		    CustomDialog.showOKCancelDialog(parent, selectPanel, "Object Selection", true, true);
		if (retValue == CustomDialog.OK_OPTION) {
			int selectedIndex = table.getSelectedRow();
			return otObjectTableModel.getObjectAt(selectedIndex);
		} else {
			return null;
		}
	}

	private static boolean contains(Object[] objectsAndClasses, Object obj)
	{
		if (objectsAndClasses == null) {
			return false;
		}

		for (int i = 0; i < objectsAndClasses.length; i++) {
			if (objectsAndClasses[i] instanceof Class) {
				// FIXME I'm pretty sure the following two statements are the same
				// perhaps they were intended to be reversed so it would check if the 
				// obj is an instance of a class, or if the object class is a super class of
				// any of the classes?			
				if (((Class<?>) objectsAndClasses[i]).isInstance(obj)
				        || ((Class<?>) objectsAndClasses[i]).isAssignableFrom(obj.getClass()))
					return true;
			} else {
				if (objectsAndClasses[i] == obj)
					return true;
			}
		}
		return false;
	}

	private static class OTObjectTableModel extends AbstractTableModel
	{
		private String[] columnNames = { "Class", "Name", "ID" };

		private Vector<OTObject> otObjects;

		public OTObjectTableModel(Vector<OTObject> otObjects)
		{
			this.otObjects = otObjects;
		}

		public int getColumnCount()
		{
			return 3;
		}

		public int getRowCount()
		{
			return otObjects.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			OTObject obj = (OTObject) otObjects.get(rowIndex);
			if (columnIndex == 0) {
				String[] classPath = obj.otClass().getName().split("\\.");
				return classPath[classPath.length - 1];
			} else if (columnIndex == 1) {
				return obj.getName();
			} else {
				return obj.getGlobalId().toExternalForm();
			}
		}

		public OTObject getObjectAt(int rowIndex)
		{
			return (OTObject) otObjects.get(rowIndex);
		}

		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		public boolean isCellEditable(int row, int col)
		{
			return false;
		}
	}
}
