package org.concord.otrunk.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

import org.concord.framework.otrunk.view.PrefersSize;

public class RespectfulMaximizingLayout implements LayoutManager, LayoutManager2 {
    public void addLayoutComponent(Component arg0, Object arg1) { }

    public float getLayoutAlignmentX(Container arg0) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container arg0) {
        return 0.5f;
    }

    public void invalidateLayout(Container arg0) { }

    public Dimension maximumLayoutSize(Container arg0) {
        Component comp = getComponent(arg0);
        if (comp != null) {
            return comp.getMaximumSize();
        }
        return arg0.getMaximumSize();
    }
    
    private Component getComponent(Container arg0) {
        if (arg0.getComponentCount() > 0) {
            return arg0.getComponent(0);
        }
        return null;
    }

    public void addLayoutComponent(String arg0, Component arg1) {
        addLayoutComponent(arg1, null);
    }

    public void layoutContainer(Container arg0) {
        if (arg0.getComponentCount() > 0) {
            Dimension maxSize = preferredLayoutSize(arg0);
            Insets insets = arg0.getInsets();
            Dimension containerSize = new Dimension(maxSize.width + insets.left + insets.right, maxSize.height + insets.top + insets.bottom);
            arg0.setSize(containerSize);
            getComponent(arg0).setBounds(insets.left, insets.top, maxSize.width, maxSize.height);
        }
    }

    public Dimension minimumLayoutSize(Container arg0) {
        if (arg0.getComponentCount() > 0) {
            return getComponent(arg0).getMinimumSize();
        }
        return arg0.getMinimumSize();
    }

    public Dimension preferredLayoutSize(Container arg0) {
        Dimension maxSize = getMaximumPossibleSize(arg0);

        Component comp = getComponent(arg0);
        if (comp instanceof PrefersSize) {
            // System.out.println("Component prefers size!");
            return ((PrefersSize)comp).requestPreferredSize(maxSize);
        } else if (comp instanceof OTViewContainerPanel && ((OTViewContainerPanel)comp).getCurrentComponent() instanceof PrefersSize) {
            // System.out.println("Current component prefers size!");
            PrefersSize currentComp = (PrefersSize)((OTViewContainerPanel)comp).getCurrentComponent();
            return currentComp.requestPreferredSize(maxSize);
        } else if (comp != null) {
            // System.out.println("Component does not prefer size");
            return comp.getPreferredSize();
        }
        return maxSize;
    }

    public void removeLayoutComponent(Component arg0) { }
    
    private Dimension getMaximumPossibleSize(Container container) {
        Dimension availableSize = container.getSize();
        Insets insets = container.getInsets();
        availableSize.height -= (insets.top + insets.bottom);
        availableSize.width -= (insets.left + insets.right);
        return availableSize;
    }

}
