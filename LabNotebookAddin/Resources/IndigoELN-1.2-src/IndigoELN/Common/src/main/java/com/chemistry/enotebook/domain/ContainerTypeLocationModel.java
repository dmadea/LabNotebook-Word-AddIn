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
package com.chemistry.enotebook.domain;

public class ContainerTypeLocationModel {
  protected String type = "";
  protected String loc = "";

  public ContainerTypeLocationModel(String type, String loc) {
    this.type = type;
    this.loc = loc;
  }

  public ContainerTypeLocationModel(String type) {
    this(type,"");
  }
  
  public ContainerTypeLocationModel(){
    this("","");
  }
  
  public String getType() {
    if (!isType()) {
      return "";
    }
    
    return type;
  }

  public void setType(String type) {
    if (type == null)
      this.type = "";
    else
      this.type = type;
  }

  public String getLocation() {
    return loc;
  }

  public void setLocation(String loc) {
    if (loc == null)
      this.loc = "";
    else
      this.loc = loc;
  }

  private boolean isType() {
    if (type == null || type.trim().length() == 0 || type.contains("None")) {
      return false;
    }
    
    return true;
  }
  
  public String toString() {
    return type;
  }
}
