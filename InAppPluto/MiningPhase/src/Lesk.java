
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.util.GlossFinder;
import edu.cmu.lti.ws4j.util.GlossFinder.SuperGloss;
import edu.cmu.lti.ws4j.util.OverlapFinder;
import edu.cmu.lti.ws4j.util.OverlapFinder.Overlaps;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

/**
 * 
 * From WS package: 
Lesk (1985) proposed that the relatedness of two words is proportional to
to the extent of overlaps of their dictionary definitions. Banerjee and
Pedersen (2002) extended this notion to use WordNet as the dictionary
for the word definitions. This notion was further extended to use the rich
network of relationships between concepts present is WordNet. This adapted
lesk measure has been implemented in this module.
 * @author Hideki
 *
 */
@SuppressWarnings("serial")
public class Lesk extends RelatednessCalculator {

	protected static double min = 0;
	protected static double max = Double.MAX_VALUE;
	private GlossFinder glossFinder;
	
	private static List<POS[]> posPairs = new ArrayList<POS[]>(){{
		add(new POS[]{POS.n,POS.n});
		add(new POS[]{POS.v,POS.v});
		add(new POS[]{POS.a,POS.a});
		add(new POS[]{POS.r,POS.r});
		add(new POS[]{POS.n,POS.v});
		add(new POS[]{POS.v,POS.n});
	}};
	
	private StringBuilder overlapLog;
	private StringBuilder overlapLogMax;
	
	public Lesk(ILexicalDatabase db) {
		super(db);
		glossFinder = new GlossFinder(db);
	}

	@Override
	protected Relatedness calcRelatedness( Concept synset1, Concept synset2 ) {
		if ( synset1 == null || synset2 == null ) return new Relatedness( min );
		//Don't short-circuit!
		//if ( synset1.getSynset().equals( synset2.getSynset() ) ) return new Relatedness( max );
		
		StringBuilder tracer = new StringBuilder();
		List<SuperGloss> glosses = glossFinder.getSuperGlosses( synset1, synset2 );
		int score = 0;
		for ( int i=0; i<glosses.size(); i++ ) {
			SuperGloss sg = glosses.get(i);
			
			double functionsScore = calcFromSuperGloss( sg.gloss1, sg.gloss2 );
			functionsScore *= glosses.get(i).weight; // default weight = 1
			
			if ( enableTrace && functionsScore > 0 ) {
				tracer.append( "Functions: "+sg.link1.trim()+" - "+sg.link2.trim()+" : "+functionsScore + "\n" );
				tracer.append( overlapLogMax+"\n\n" );				
			}
			
			score += functionsScore;
		}
				
		return new Relatedness( score, tracer.toString(), null );
	}
	
	private double calcFromSuperGloss( List<String> glosses1, List<String> glosses2 ) {
		double max = 0;
		overlapLogMax = new StringBuilder();
		for ( String gloss1 : glosses1 ) {
			for ( String gloss2 : glosses2 ) {
				double score = calcFromSuperGloss( gloss1, gloss2 );
				if ( max < score ) {
					overlapLogMax = overlapLog;
					max = score;
				}
			}
		}
		return max;
	}
	
	private double calcFromSuperGloss( String gloss1, String gloss2 ) {
		// Stopwords are removed inside following method. 
		Overlaps overlaps = OverlapFinder.getOverlaps( gloss1, gloss2 );
		
		double functionsScore = 0;
		if ( enableTrace ) overlapLog = new StringBuilder( "Overlaps: " );
		for ( String key : overlaps.overlapsHash.keySet() ) {
			String[] tempArray = key.split("\\s+");
			int value = (tempArray.length) * (tempArray.length) * overlaps.overlapsHash.get(key);
			functionsScore += value;
			if ( enableTrace ) {
				overlapLog.append( overlaps.overlapsHash.get(key)+" x \""+key+"\" " );
			}
		}
		if ( enableTrace ) overlapLog = new StringBuilder( "\n" );
		
		if ( WS4JConfiguration.getInstance().useLeskNomalizer() ) {
			int denominator = overlaps.length1 + overlaps.length2;
			if  ( denominator > 0 ) functionsScore /= (double)denominator; 
			if ( enableTrace ) {
				overlapLog.append( "Normalized by dividing the score with "+overlaps.length1+" and "+overlaps.length2+"\n" );
			}
		}
		
		return functionsScore;
	}
	
	@Override
	public List<POS[]> getPOSPairs() {
		return posPairs;
	}
}
