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
package com.chemistry.enotebook.storage.jdbc.update;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.sql.Types;

public class ReactionSchemeUpdate extends SqlUpdate {
	private static final Log log = LogFactory.getLog(ReactionSchemeUpdate.class);

	public ReactionSchemeUpdate(DataSource dsource) {
		super(dsource, getUpdateSql());
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // VRXN_ID
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // PROTOCOL_ID
		this.declareParameter(new SqlParameter(Types.BINARY)); // RXN_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // NATIVE_RXN_SKETCH
		this.declareParameter(new SqlParameter(Types.BINARY)); // SKETCH_IMAGE
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // XML_METADATA ( no attribs saved anymore )
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //RXN_SKTH_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //NATIVE_RXN_SKTH_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //RXN_IMAGE_FRMT
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //SYNTH_ROUTE_REF	
		this.declareParameter(new SqlParameter(Types.VARCHAR)); //REACTION_ID
		
		this.declareParameter(new SqlParameter(Types.VARCHAR)); // RXN_SCHEME_KEY

	}

	private static String getUpdateSql() {
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("UPDATE CEN_REACTION_SCHEMES SET ");
		updateSql.append(" VRXN_ID=?");
		updateSql.append(" ,PROTOCOL_ID=?");
		updateSql.append(" ,RXN_SKETCH=? ");
		updateSql.append(" ,NATIVE_RXN_SKETCH=? ");
		updateSql.append(" ,SKETCH_IMAGE=? ");
		updateSql.append(" ,XML_METADATA=?" );
		updateSql.append(" ,RXN_SKTH_FRMT=? ");
		updateSql.append(" ,NATIVE_RXN_SKTH_FRMT=? ");
		updateSql.append(" ,RXN_IMAGE_FRMT=? ");
		updateSql.append(" ,SYNTH_ROUTE_REF=? ");
		updateSql.append(" ,REACTION_ID=? ");
		updateSql.append(" WHERE RXN_SCHEME_KEY=?");
		log.debug(updateSql);
		return updateSql.toString();
	}


}