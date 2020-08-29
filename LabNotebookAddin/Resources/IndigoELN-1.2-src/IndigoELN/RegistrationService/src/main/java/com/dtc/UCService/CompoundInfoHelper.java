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
package com.dtc.UCService;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

// Referenced classes of package com.dtc.UCService:
//            CompoundInfo

public final class CompoundInfoHelper
{

    public CompoundInfoHelper()
    {
    }

    public static TypeCode type()
    {
        if(_type == null)
            _type = ORB.init().create_struct_tc(id(), "CompoundInfo", new StructMember[] {
                new StructMember("compNumber", ORB.init().create_string_tc(0), null), new StructMember("isomerCode", ORB.init().create_string_tc(0), null)
            });
        return _type;
    }

    public static void insert(Any any, CompoundInfo s)
    {
        any.type(type());
        write(any.create_output_stream(), s);
    }

    public static CompoundInfo extract(Any any)
    {
        return read(any.create_input_stream());
    }

    public static String id()
    {
        return "IDL:com/dtc/UCService/CompoundInfo:1.0";
    }

    public static CompoundInfo read(InputStream in)
    {
        CompoundInfo result = new CompoundInfo();
        result.compNumber = in.read_string();
        result.isomerCode = in.read_string();
        return result;
    }

    public static void write(OutputStream out, CompoundInfo s)
    {
        out.write_string(s.compNumber);
        out.write_string(s.isomerCode);
    }

    private static TypeCode _type = null;

}
