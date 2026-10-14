##
### ABOUT JAXB GENERATION
##
    
jaxb classes are generated like this:

xjc 
    -d src/main/java/ 
    -b src/main/resources/org/cmdbuild/workflow/river/engine/xpdl/schema/bindings.xml
    src/main/resources/org/cmdbuild/workflow/river/engine/xpdl/schema/bpmnxpdl_31a.xsd 

xjc version is:
    xjc 2.2.8-b130911.1802

