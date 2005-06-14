/**
 * KBSOAPPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package KBSOAP;

public interface KBSOAPPort extends java.rmi.Remote {
    public java.lang.String helloWorld(java.lang.String login) throws java.rmi.RemoteException;
    public KBSOAP.DosearchResponse doSearch(java.lang.String login, java.lang.String whichkb, java.lang.String query, java.lang.String advanced, java.lang.String[] domains, java.lang.String audience, java.lang.String archived) throws java.rmi.RemoteException;
    public KBSOAP.KbDocumentTitleInfo getTitles(java.lang.String login, java.lang.String whichkb, java.lang.String[] docIds, java.lang.String audience) throws java.rmi.RemoteException;
    public KBSOAP.KbHotitemInfo getHotitems(java.lang.String login, java.lang.String whichkb) throws java.rmi.RemoteException;
    public KBSOAP.KbDocumentInfo getDocument(java.lang.String login, java.lang.String whichkb, java.lang.String docId, java.lang.String[] domains, java.lang.String format, java.lang.String urlBase, java.lang.String audience) throws java.rmi.RemoteException;
}
