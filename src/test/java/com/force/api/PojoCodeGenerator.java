/*
 * Copyright (c) 2011, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.force.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.force.api.ApiVersion;
import com.force.api.DescribeSObject;
import com.force.api.DescribeSObject.Field;

/**
 * Generates plain old java objects from api describe calls
 * 
 * @author gwester
 * @since 172
 */
public class PojoCodeGenerator {
    
    static final String NEWLINE = "\r\n";
    static final String TAB = "    ";

    private final void write(OutputStream out, String content) throws IOException {
    	out.write(content.getBytes("UTF-8"));
    }
    
    public boolean generateCode(OutputStream out, DescribeSObject describe, String apiVersion, String packageName) throws IOException {
        if(describe == null) {
            return false;
        }

        
        String className = describe.getName();
        if(describe.isCustom()) {
        	className = className.substring(0,className.length()-3);
        }

        if(packageName == null || packageName.isEmpty()) {
        	if(describe.isCustom()) {
        		packageName = "org.example.sobject";
        	} else {
        		packageName = "com.salesforce.sobject";
        	}
        }
        
        Set<String> fieldNames = new HashSet<String>();
        for(Field f : describe.getAllFields()) {
        	fieldNames.add(f.getName());
        }

        
        // package
        write(out,"package " + packageName + ";" + NEWLINE + NEWLINE);

        // class comment block
        write(out,"/**" + NEWLINE);
        write(out," * Generated from information gathered from /services/data/" + apiVersion + 
                "/sobjects/" + describe.getName() + "/describe" + NEWLINE);
        write(out," */" + NEWLINE);
        
        // class begin
        write(out,"public class " + className + " {" + NEWLINE);

        // constants
        write(out,TAB + "public static boolean CREATEABLE = "); 
        write(out,describe.isCreateable() ? "true;" : "false;");
        write(out,NEWLINE);

        write(out,TAB + "public static boolean DELETABLE = ");
        write(out,describe.isDeletable() ? "true;" : "false;");
        write(out,NEWLINE);

        write(out,TAB + "public static boolean UPDATEABLE = ");
        write(out,describe.isUpdateable() ? "true;" : "false;");
        write(out,NEWLINE);
        
        write(out,NEWLINE);

        // add all private member variables
        for (Field field : describe.getAllFields()) {
            String fieldNameLower = 
                field.getName().substring(0, 1).toLowerCase()
                    + field.getName().substring(1, field.getName().length());

            // write private member
            write(out,TAB + "private " + getJavaType(field) + " " + fieldNameLower + ";" + NEWLINE);
            
        }
        
        // no arg constructor
        write(out,NEWLINE + TAB + "/**" + NEWLINE);
        write(out,TAB + " * Constructor." + NEWLINE);
        write(out,TAB + " */" + NEWLINE);
        write(out,TAB + "public " + className + "() { }" + NEWLINE);
        
        // constructor with required fields
        int count = 0;
        int max = describe.getRequiredFieldsForCreateUpdate().size();
        if(max > count) {
            
            write(out,NEWLINE + TAB + "/**" + NEWLINE);
            write(out,TAB + " * Constructor with required fields." + NEWLINE);
            write(out,TAB + " */" + NEWLINE);
            write(out,TAB + "public " + className + "(");
           
            for (Field field : describe.getRequiredFieldsForCreateUpdate()) {
                String fieldNameLower = field.getName().substring(0, 1).toLowerCase()
                        + field.getName().substring(1, field.getName().length());
   
                write(out,getJavaType(field) + " " + fieldNameLower);
                if (++count != max) { write(out,", "); }
            }
            write(out,") {" + NEWLINE);
            
            //constructor body
            for (Field field : describe.getRequiredFieldsForCreateUpdate()) {
                String fieldNameLower = field.getName().substring(0, 1).toLowerCase()
                        + field.getName().substring(1, field.getName().length());
    
                write(out,TAB + TAB + "this." + fieldNameLower + " = " + fieldNameLower + ";" + NEWLINE);
            }
            write(out,TAB + "}" + NEWLINE + NEWLINE);
        }

        // add getters for every private member variable (no need to hide anything)
        for (Field field : describe.getAllFields()) {
            String fieldNameLower = field.getName().substring(0, 1).toLowerCase()
                    + field.getName().substring(1, field.getName().length());

            write(out,TAB + "public " + getJavaType(field) + " get" + capitalize(field.getName()) + "() {"
                    + NEWLINE);
            write(out,TAB + TAB + "return " + fieldNameLower + ";" + NEWLINE);
            write(out,TAB + "}" + NEWLINE);
            
            // for custom fields, add an alias getter without __c unless it conflicts with a standard field name
            if(field.isCustom()) {
                String aliasedName = field.getName().substring(0,field.getName().length()-3);
                if(!fieldNames.contains(aliasedName)) {
	                write(out,TAB+ "/**" + NEWLINE);
	            	write(out,TAB+ " * alias for #get"+capitalize(fieldNameLower) + NEWLINE);
	                write(out,TAB+ " */" + NEWLINE);
	                write(out,TAB + "public " + getJavaType(field) + " get" + capitalize(aliasedName) + "() {"
	                        + NEWLINE);
	                write(out,TAB + TAB + "return " + fieldNameLower + ";" + NEWLINE);
	                write(out,TAB + "}" + NEWLINE);
                }
            }
        }

        // add setters for all fields
        for (Field field : describe.getAllFields()) {

        	if(!field.isCreateable()) {
        		continue;
        	}

        	String fieldNameUpper = field.getName();
            String fieldNameLower = field.getName().substring(0, 1).toLowerCase()
                    + field.getName().substring(1, field.getName().length());

            write(out,TAB+ "/**" + NEWLINE);
            if ((!field.isNillable()) && (!field.isDefaultedOnCreate())) {
            	write(out,TAB + " * " + fieldNameUpper + " is a required field." + NEWLINE);
            } else {
            	if(field.getRelationshipName() == null) {
                    write(out,TAB + " * " + fieldNameUpper + " is an optional field." + NEWLINE);
            	} else {
                    write(out,TAB + " * " + fieldNameUpper + " is a reference to a parent entity." + NEWLINE);
            	}
            }
            write(out,TAB+ " */" + NEWLINE);
            write(out,TAB + "public void set" + capitalize(fieldNameUpper) + "(" + getJavaType(field)
                    + " " + fieldNameLower + ") {" + NEWLINE);
            write(out,TAB + TAB + "this." + fieldNameLower + " = " + fieldNameLower + ";" + NEWLINE);
            write(out,TAB + "}" + NEWLINE);

            // for custom fields, add an alias setter without __c unless it conflicts with a standard field name
            if(field.isCustom()) {
	            String aliasedName = fieldNameUpper.substring(0,fieldNameUpper.length()-3);
	            if(!fieldNames.contains(aliasedName)) {
	                write(out,TAB+ "/**" + NEWLINE);
	            	write(out,TAB+ " * alias for #set"+capitalize(fieldNameUpper) + NEWLINE);
	                write(out,TAB+ " */" + NEWLINE);
	                write(out,TAB + "public void set" + capitalize(aliasedName) + "("+ getJavaType(field)
	                	    + " " + fieldNameLower + ") { "+ NEWLINE);
	                write(out,TAB + TAB + "this." + fieldNameLower + " = " + fieldNameLower + ";" + NEWLINE);
	                write(out,TAB + "}" + NEWLINE);
	            }
            }

        }

        // Note: there are no setters for things that can never be set, like Ids and system fields

        // class end
        write(out,"}" + NEWLINE);
        
        return true;
    }
	
    /**
     * This method attempts to match the salesforce sobject field type to Java. If it doesn't know, it returns String.
     * 
     * @param salesforceFieldType
     * @return
     */
    private String getJavaType(Field field) {
        if (field.getType().equals("int")) {
            return "Integer";
        } 
        else if (field.getType().equals("boolean")) {
            return "Boolean";
        } 
        else if (field.getType().equals("double")) {
            return "Double";
        } 
        else {
            return "String";
        }
    }
    
    private String capitalize(String in) {
    	if(Character.isUpperCase(in.indexOf(0))) { 
    		return in; 
    	} else {
    		return new String(Character.toUpperCase(in.charAt(0))+in.substring(1,in.length()));
    	}
    	
    }
}
