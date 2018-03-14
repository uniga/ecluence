package dk.uniga.ecluence.core;

public interface PageContentProcessor {

	String process(String content) throws PageContentProcessingException;

}