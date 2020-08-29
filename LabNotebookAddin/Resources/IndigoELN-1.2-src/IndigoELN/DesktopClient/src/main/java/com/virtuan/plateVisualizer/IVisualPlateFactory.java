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
package com.virtuan.plateVisualizer;

/**
 * Title: IVisualPlateFactory Description: This is an interface for the Platefactories.
 */
public abstract interface IVisualPlateFactory {
	public WellMood createFilledWellMood(String moodType);

	public WellMood createEmptyWellMood(String moodType);

	public PlateMood createPlateMood(String moodType, String title);
}
