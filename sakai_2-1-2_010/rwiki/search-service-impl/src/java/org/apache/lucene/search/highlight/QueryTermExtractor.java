package org.apache.lucene.search.highlight;
/**
 * Copyright 2002-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;

/**
 * Utility class used to extract the terms used in a query, plus any weights.
 * This class will not find terms for MultiTermQuery, RangeQuery and PrefixQuery classes
 * so the caller must pass a rewritten query (see Query.rewrite) to obtain a list of 
 * expanded terms. 
 * 
 */
public final class QueryTermExtractor
{

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @return an array of the terms used in a query, plus their weights.
	 */
	public static final WeightedTerm[] getTerms(Query query) 
	{
		return getTerms(query,false);
	}

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param reader used to compute IDF which can be used to a) score selected fragments better 
	 * b) use graded highlights eg chaning intensity of font color
	 * @param fieldName the field on which Inverse Document Frequency (IDF) calculations are based
	 * @return an array of the terms used in a query, plus their weights.
	 */
	public static final WeightedTerm[] getIdfWeightedTerms(Query query, IndexReader reader, String fieldName) 
	{
	    WeightedTerm[] terms=getTerms(query,false, fieldName);
	    int totalNumDocs=reader.numDocs();
	    for (int i = 0; i < terms.length; i++)
        {
	        try
            {
                int docFreq=reader.docFreq(new Term(fieldName,terms[i].term));
                //IDF algorithm taken from DefaultSimilarity class
                float idf=(float)(Math.log((float)totalNumDocs/(double)(docFreq+1)) + 1.0);
                terms[i].weight*=idf;
            } 
	        catch (IOException e)
            {
	            //ignore 
            }
        }
		return terms;
	}

	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param prohibited <code>true</code> to extract "prohibited" terms, too
	 * @param fieldName  The fieldName used to filter query terms
   * @return an array of the terms used in a query, plus their weights.
   */
	public static final WeightedTerm[] getTerms(Query query, boolean prohibited, String fieldName) 
	{
		HashSet terms=new HashSet();
		if(fieldName!=null)
		{
		    fieldName=fieldName.intern();
		}
		getTerms(query,terms,prohibited,fieldName);
		return (WeightedTerm[]) terms.toArray(new WeightedTerm[0]);
	}
	
	/**
	 * Extracts all terms texts of a given Query into an array of WeightedTerms
	 *
	 * @param query      Query to extract term texts from
	 * @param prohibited <code>true</code> to extract "prohibited" terms, too
   * @return an array of the terms used in a query, plus their weights.
   */
	public static final WeightedTerm[] getTerms(Query query, boolean prohibited) 
	{
	    return getTerms(query,prohibited,null);
	}	

	//fieldname MUST be interned prior to this call
	private static final void getTerms(Query query, HashSet terms,boolean prohibited, String fieldName) 
	{
		if (query instanceof BooleanQuery)
			getTermsFromBooleanQuery((BooleanQuery) query, terms, prohibited, fieldName);
		else
			if (query instanceof PhraseQuery)
				getTermsFromPhraseQuery((PhraseQuery) query, terms, fieldName);
			else
				if (query instanceof TermQuery)
					getTermsFromTermQuery((TermQuery) query, terms, fieldName);
				else
		        if(query instanceof SpanNearQuery)
		            getTermsFromSpanNearQuery((SpanNearQuery) query, terms, fieldName);
	}

	private static final void getTermsFromBooleanQuery(BooleanQuery query, HashSet terms, boolean prohibited, String fieldName)
	{
		BooleanClause[] queryClauses = query.getClauses();
		int i;

		for (i = 0; i < queryClauses.length; i++)
		{
			//Pre Lucene 2.0 code
			if (prohibited || !queryClauses[i].prohibited)
				getTerms(queryClauses[i].query, terms, prohibited, fieldName);
			// Lucene 2.0 ready code
//			if (prohibited || queryClauses[i].getOccur()!=BooleanClause.Occur.MUST_NOT)
//				getTerms(queryClauses[i].getQuery(), terms, prohibited, fieldName);
		}
	}

	private static final void getTermsFromPhraseQuery(PhraseQuery query, HashSet terms, String fieldName)
	{
		Term[] queryTerms = query.getTerms();
		int i;

		for (i = 0; i < queryTerms.length; i++)
		{
		    if((fieldName==null)||(queryTerms[i].field()==fieldName))
		    {
		        terms.add(new WeightedTerm(query.getBoost(),queryTerms[i].text()));
		    }
		}
	}

	private static final void getTermsFromTermQuery(TermQuery query, HashSet terms, String fieldName)
	{
	    if((fieldName==null)||(query.getTerm().field()==fieldName))
	    {
	        terms.add(new WeightedTerm(query.getBoost(),query.getTerm().text()));
	    }
	}

    private static final void getTermsFromSpanNearQuery(SpanNearQuery query, HashSet terms, String fieldName){

        Collection queryTerms = query.getTerms();

        for(Iterator iterator = queryTerms.iterator(); iterator.hasNext();){

            // break it out for debugging.

            Term term = (Term) iterator.next();

            String text = term.text();

    	    if((fieldName==null)||(term.field()==fieldName))
    	    {
    	        terms.add(new WeightedTerm(query.getBoost(), text));
    	    }
        }

    }

}
