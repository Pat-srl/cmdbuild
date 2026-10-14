package org.cmdbuild.logic.mapping.json;

public final class Constants {

    private Constants() {
        // empty...created only to prevent instantiation
    }

    /**
     * JSON filter keys
     */
    public final class Filters {

        private Filters() {
            // prevents instantiation
        }

        public static final String FILTER_KEY = "filter";
        public static final String ATTRIBUTE_KEY = "attribute";
        public static final String CLASSNAME_KEY = "ClassName";
        public static final String FULL_TEXT_QUERY_KEY = "query";
        public static final String RELATION_KEY = "relation";
        public static final String ATTACHMENT_KEY = "attachment";
        public static final String COMPOSITE_KEY = "composite";
        public static final String MODE_KEY = "mode";
        public static final String ELEMENTS_KEY = "elements";
        public static final String CQL_KEY = "CQL";
        public static final String FUNCTION_KEY = "functions";
        public static final String SIMPLE_KEY = "simple";
        public static final String AND_KEY = "and";
        public static final String OR_KEY = "or";
        public static final String NOT_KEY = "not";
        public static final String OPERATOR_KEY = "operator";
        public static final String VALUE_KEY = "value";
        public static final String RELATION_DOMAIN_KEY = "domain";
        public static final String RELATION_DOMAIN_DIRECTION = "direction";
        public static final String RELATION_TYPE_KEY = "type";
        public static final String RELATION_CARDS_KEY = "cards";
        public static final String RELATION_FILTER = "filter";
        public static final String RELATION_TYPE_ANY = "any";
        public static final String RELATION_TYPE_NOONE = "noone";
        public static final String RELATION_TYPE_ONEOF = "oneof";
        public static final String RELATION_CARD_ID_KEY = "id";
        public static final String RELATION_CARD_CLASSNAME_KEY = "className";
        public static final String FUNCTION_NAME_KEY = "name";

        public static final String ECQL_KEY = "ecql",
                ECQL_ID_KEY = "id",
                ECQL_CONTEXT_KEY = "context";

        public static final String _TYPE = "_type";

    }

    /**
     * JSON sorters keys
     */
    public static final String PROPERTY_KEY = "property";
    public static final String DIRECTION_KEY = "direction";

}
