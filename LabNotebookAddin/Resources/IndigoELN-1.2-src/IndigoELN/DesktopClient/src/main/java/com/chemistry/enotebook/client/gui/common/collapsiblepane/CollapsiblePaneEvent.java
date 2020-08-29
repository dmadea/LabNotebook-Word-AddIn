/****************************************************************************
 * Copyright (C) 2009-2015 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/
package com.chemistry.enotebook.client.gui.common.collapsiblepane;

import java.awt.*;

public class CollapsiblePaneEvent extends AWTEvent
{
  public static final int COLLAPSIBLE_PANE_FIRST = 5099;
  public static final int COLLAPSIBLE_PANE_LAST = 5103;
  public static final int COLLAPSIBLE_PANE_EXPANDING = 5099;
  public static final int COLLAPSIBLE_PANE_EXPANDED = 5100;
  public static final int COLLAPSIBLE_PANE_COLLAPSING = 5101;
  public static final int COLLAPSIBLE_PANE_COLLAPSED = 5102;
  public static int a;

  public CollapsiblePaneEvent(Object paramObject, int paramInt)
  {
    super(paramObject, paramInt);
  }
}