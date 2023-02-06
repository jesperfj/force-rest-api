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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

/**
 * This class represents a JSON response from /services/data/v{version}/sobjects/{sobjectName}/describe It has all of
 * the fields for a particular sobject, and metadata about those fields.
 * 
 * @author gwester
 * @since 172
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DescribeSObject extends DescribeSObjectBasic {

    private List<Field> fields;
    private List<ChildEntity> childRelationships;

    public List<Field> getAllFields() {
        return fields;
    }

    public List<Field> getFields() {
        return fields;
    }
    
    public List<ChildEntity> getChildRelationships() {
    	return childRelationships;
    }

    /**
     * 
     * @return A map keyed child entities (e.g. Opportunity), with value of relationship name (e.g. childOpportunities).
     */
    public Map<String, String> getChildEntities() {
        Map<String, String> children = new HashMap<String, String>();
        
        for(ChildEntity child : childRelationships) {
            //skip parents
            if(child.getField().equals("AccountId")) {
                if(child.getRelationshipName() != null) {       
                    children.put(child.getChildSObject(), child.getRelationshipName());
                }
                else {
                    //TODO: figure out if we're going to do null relationships
                    children.put(child.getChildSObject(), child.getChildSObject() + "s");
                }
            }
        }
        return children;
    }


    public Set<Field> getRequiredFieldsForCreateUpdate() {
        Set<Field> required = new HashSet<Field>();
        for (Field field : fields) {
            if (field.isCreateable() && (!field.isNillable()) && (!field.isDefaultedOnCreate())) {
                required.add(field);
            }
        }
        return required;
    }

    public Set<Field> getOptionalFieldsForCreateUpdate() {
        Set<Field> optional = new HashSet<Field>();
        for (Field field : fields) {
            if (field.isCreateable() && (field.isNillable() || field.isDefaultedOnCreate())) {
                if(field.getRelationshipName() == null) {
                    optional.add(field);
                }
            }
        }
        return optional;
    }
    
    public Set<Field> getParentEntitiesForCreateUpdate() {
        Set<Field> parentReference = new HashSet<Field>();
        for (Field field : fields) {
            if (field.isCreateable() && (field.isNillable() || field.isDefaultedOnCreate())) {
                if(field.getRelationshipName() != null) {
                    parentReference.add(field);
                }
            }
        }
        return parentReference;
    }

    /**
     * This class represents part of a JSON response from /services/data/v{version}/sobjects/{sobjectName}/describe
     * 
     * @author gwester
     * @since 170
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Field {
        private Integer length;
        private String name;
        private String type;
        private String soapType;
        private String defaultValue;
        private String label;
        private Boolean updateable;
        private Boolean calculated;
        private Boolean unique;
        private Boolean nillable;
        private Boolean caseSensitive;
        private String inlineHelpText;
        private Boolean nameField;
        private Boolean externalId;
        private Boolean idLookup;
        private Boolean filterable;
        // soapType;
        private Boolean createable;
        private Boolean deprecatedAndHidden;
        private List<PicklistEntry> picklistValues;
        private Boolean autoNumber;
        private Boolean defaultedOnCreate;
        private Boolean groupable;
        private String relationshipName;
        private List<String> referenceTo;
        // relationshipOrder;
        private Boolean restrictedPicklist;
        private Boolean namePointing;
        private Boolean custom;
        private Boolean htmlFormatted;
        private Boolean dependentPicklist;
        private Boolean writeRequiresMasterRead;
        private Boolean sortable;

        public Integer getLength() {
            return length;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getSoapType() {
            return soapType;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getLabel() {
            return label;
        }

        public Boolean isUpdateable() {
            return updateable;
        }

        public Boolean isCalculated() {
            return calculated;
        }

        public Boolean isUnique() {
            return unique;
        }

        public Boolean isNillable() {
            return nillable;
        }

        public Boolean isCaseSensitive() {
            return caseSensitive;
        }

        public String getInlineHelpText() {
            return inlineHelpText;
        }

        public Boolean isNameField() {
            return nameField;
        }

        public Boolean isExternalId() {
            return externalId;
        }

        public Boolean isIdLookup() {
            return idLookup;
        }

        public Boolean isFilterable() {
            return filterable;
        }

        public Boolean isCreateable() {
            return createable;
        }

        public Boolean isDeprecatedAndHidden() {
            return deprecatedAndHidden;
        }

        public List<PicklistEntry> getPicklistValues() {
            return picklistValues;
        }

        public Boolean isAutoNumber() {
            return autoNumber;
        }

        public Boolean isDefaultedOnCreate() {
            return defaultedOnCreate;
        }

        public Boolean isGroupable() {
            return groupable;
        }

        public String getRelationshipName() {
            return relationshipName;
        }
        
        public List<String> getReferenceTo() {
            return referenceTo;
        }

        public List<String> getReferenceToEntity() {
            return referenceTo;
        }

        public Boolean isRestrictedPicklist() {
            return restrictedPicklist;
        }

        public Boolean isNamePointing() {
            return namePointing;
        }

        public Boolean isCustom() {
            return custom;
        }

        public Boolean isHtmlFormatted() {
            return htmlFormatted;
        }

        public Boolean isDependentPicklist() {
            return dependentPicklist;
        }

        public Boolean isWriteRequiresMasterRead() {
            return writeRequiresMasterRead;
        }

        public Boolean isSortable() {
            return sortable;
        }
    }

    /**
     *
     * This class represents a picklist value as given by a describe api call.
     *
     * @author jjauregui
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PicklistEntry {
        private String value;
        private Boolean active;
        private String label;
        private Boolean defaultValue;
        private byte[] validFor;

        public String getValue() {
            return value;
        }

        public Boolean isActive() {
            return active;
        }

        public String getLabel() {
            return label;
        }

        public Boolean isDefaultValue() {
            return defaultValue;
        }

        public byte[] getValidFor() {
            return validFor;
        }
    }
    
    /**
     * 
     * Child Relationships.
     *
     * @author gwester
     * @since 170
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChildEntity {
        private String field;
        private String childSObject;
        private String relationshipName;
        private Boolean deprecatedAndHidden;
        private Boolean cascadeDelete;
        
        public String getField() {
            return field;
        }
        
        public String getChildSObject() {
            return childSObject;
        }
        
        public String getRelationshipName() {
            return relationshipName;
        }
        
        public Boolean isDeprecatedAndHidden() {
            return deprecatedAndHidden;
        }
        
        public Boolean isCascadeDelete() {
            return cascadeDelete;
        }
    }
}
