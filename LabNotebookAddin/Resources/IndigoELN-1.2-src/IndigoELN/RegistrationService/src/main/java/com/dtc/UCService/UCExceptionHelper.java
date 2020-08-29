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

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

// Referenced classes of package com.dtc.UCService:
//            UCException, CompoundInfo, CompoundInfoHelper

public final class UCExceptionHelper
{

    public UCExceptionHelper()
    {
    }

    public static TypeCode type()
    {
        if(_type == null)
            _type = ORB.init().create_exception_tc(id(), "UCException", new StructMember[] {
                new StructMember("ucError", ORB.init().create_string_tc(0), null), new StructMember("errCode", ORB.init().get_primitive_tc(TCKind.from_int(3)), null), new StructMember("ciList", ORB.init().create_sequence_tc(0, CompoundInfoHelper.type()), null)
            });
        return _type;
    }

    public static void insert(Any any, UCException s)
    {
        any.type(type());
        write(any.create_output_stream(), s);
    }

    public static UCException extract(Any any)
    {
        return read(any.create_input_stream());
    }

    public static String id()
    {
        return "IDL:com/dtc/UCService/UCException:1.0";
    }

    public static UCException read(InputStream in)
    {
        UCException result = new UCException();
        if(!in.read_string().equals(id()))
            throw new MARSHAL("wrong id");
        result.ucError = in.read_string();
        result.errCode = in.read_long();
        int _lresult_ciList4 = in.read_long();
        result.ciList = new CompoundInfo[_lresult_ciList4];
        for(int i = 0; i < result.ciList.length; i++)
            result.ciList[i] = CompoundInfoHelper.read(in);

        return result;
    }

    public static void write(OutputStream out, UCException s)
    {
        out.write_string(id());
        out.write_string(s.ucError);
        out.write_long(s.errCode);
        out.write_long(s.ciList.length);
        for(int i = 0; i < s.ciList.length; i++)
            CompoundInfoHelper.write(out, s.ciList[i]);

    }

    private static TypeCode _type = null;

}
