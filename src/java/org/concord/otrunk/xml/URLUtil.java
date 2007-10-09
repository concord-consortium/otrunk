package org.concord.otrunk.xml;

import java.net.URL;

public class URLUtil
{

	public static String getRelativeURL(URL context, URL url)
    {
    	String urlStr = url.toExternalForm();
    			
    	// there is also the URI relativize method that could be used here
    	// but it won't do .. notation
    	
    	if(context.getProtocol() == null || 
    			!context.getProtocol().equals(url.getProtocol())){
    		return urlStr;
    	}
    
    	// As far as I can tell the authority includes the host and the port		
    	if(context.getAuthority() != null && !context.getAuthority().equals("") &&
    			!context.getAuthority().equals(url.getAuthority())){
    		return urlStr;
    	}
    
    	String contextPath = context.getPath();
    	String urlPath = url.getPath();
    
    	// The "split" method used below creates an empty string
    	// at the beginning if the url starts with 
    	String strippedContextPath = contextPath;
    	String strippedUrlPath = urlPath;
    	if(contextPath.startsWith("/")){
    		strippedContextPath = contextPath.substring(1);
    		if(urlPath.startsWith("/")){
    			strippedUrlPath = urlPath.substring(1);
    		} else {
    			// The later code makes the assumption that if codebase path starts with 
    			// a / then the url path will too.  So if that isn't true then we
    			// throw an IllegalStateException until we figure out what to do here.
    			throw new IllegalStateException("contextPath and urlPath are not consistent " +
    					"context: "+ context + " url: " + url);
    			
    		}
    	}
    	
    	// If the contextPath doesn't end with a slash then it is considered a file
    	// so url is relative to the directory of the context.
    	String codebasePath = strippedContextPath;
    	if(!strippedContextPath.endsWith("/")){
    		int slashIndex = strippedContextPath.lastIndexOf('/');
    		if(slashIndex < 0){
    			// no slash so the context file is at the root of the authority
    			// If we are here it is because the two urls have the same authority
    			// so we can return a relative url that is the file without the leading slash
    			// if we set the codebasePath to "/" then the split method will return 
    			// a empty array which will have the desired effect below
    			codebasePath = "/";
    		} else if(slashIndex == 0){
    			// there is a slash at the beginning of the path, what does that mean?
    			throw new IllegalStateException("Last slash at the beginning of the path: " + context);
    		} else {
    			codebasePath = strippedContextPath.substring(0, slashIndex);
    		}
    	}
    	
    	String[] codebasePathSegments = codebasePath.split("/");
    	String[] urlPathSegment = strippedUrlPath.split("/");
    	
    	int i = 0;
    	for(; i<codebasePathSegments.length; i++){
    		if(i >= urlPathSegment.length){
    			break;
    		}
    		
    		if(!codebasePathSegments[i].equals(urlPathSegment[i])){
    			break;
    		}
    	}
    	
    	
    	// Handle the case where they don't match at all
    	// if the both start with / then there will be one matching
    	// segment "".  So we special case that one.
    	int matchingSegments = i;
    	
    	if(matchingSegments == 0){
    		String relativeUrl = url.getFile();
    		if(url.getRef() != null){
    			relativeUrl += "#" + url.getRef();
    		}
    		
    		// if the codebase is the root of the authority then we should strip off the 
    		// leading slash of the relative url 
    		if(codebasePathSegments.length == 0 && relativeUrl.startsWith("/")){
    			relativeUrl = relativeUrl.substring(1);
    		}
    		
    		return relativeUrl;
    	}
    
    	int codebaseNonMatchingSegments = codebasePathSegments.length - (matchingSegments);
    	String parentString = "";
    	for(int j=0; j<codebaseNonMatchingSegments; j++){
    		parentString += "../";
    	}
    	
    	String relativeUrl = parentString;
    	int urlNonMatchingSegments = urlPathSegment.length - (matchingSegments);
    	for(int j=0; j<urlNonMatchingSegments; j++){
    		relativeUrl += urlPathSegment[matchingSegments + j] + "/";
    	}
    	
    	if(urlPath.endsWith("/") && !relativeUrl.endsWith("/")){
    		relativeUrl += "/";
    	} else if(!urlPath.endsWith("/") && relativeUrl.endsWith("/")){
    		relativeUrl = relativeUrl.substring(0, relativeUrl.length()-1);
    	}
    	
    	if(url.getQuery() != null){
    		relativeUrl += "?" + url.getQuery();
    	}
    
    	if(url.getRef()  != null){
    		relativeUrl += "#" + url.getRef();
    	}
    	
    	return relativeUrl;		
    }

}
