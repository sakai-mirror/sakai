package org.sakaiproject.metaobj.shared;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Aug 10, 2005
 * Time: 5:21:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ArtifactFinderManager {
   ArtifactFinder getArtifactFinderByType(String key);
}
