import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;


public class XMLParser {
	public static String element_name = "";
	
	public static ArrayList<FoundOpportunity> parse(final File file, final IDictionary dict) {
		// TODO Auto-generated method stub
		String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
		//InAppAttributeExtractor.logger.debug("Parsing xml file " + filename);
		
		final ArrayList<FoundOpportunity> found_attributes = new ArrayList<FoundOpportunity>();
		
		
		try {
			 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		 
			DefaultHandler handler = new DefaultHandler() {
			 
				public void startElement(String uri, String localName,String qName, 
			                Attributes attributes) throws SAXException {
			 
					XMLParser.element_name = qName;
					
					/* Loop through the element's attributes */
					for(int i = 0; i < attributes.getLength(); i++){
						String attr_name = attributes.getLocalName(i);
						String attr_name_value = attributes.getValue(i);
						
						//System.out.println("Element :" + qName + ", Attribute: " + attr_name + ", Attribute value: " + attr_name_value);
						
						if(attr_name.compareTo("name") == 0){
							/* Split name if necessary to multiple names */
							ArrayList<String> subNames = Utils.getSubNames(attr_name_value);
							
							
							for(String subName : subNames){
								String resultOpportunity = "";
								if((InAppAttributeExtractor.opportunities.containsKey(subName))){
									FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), subName, subName);
									foundOpportunity.setXMLdata(XMLParser.element_name, attr_name_value, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE);
									found_attributes.add(foundOpportunity);
								}
								else{
									//check synonyms
									ArrayList<String> colSynonymsAndHypernyms = Utils.getSynonyms(subName, POS.NOUN, dict);
									//ArrayList<String> colSynonymsAndHypernyms = Utils.getHypernyms(subColumnName, POS.NOUN, dict);
									//colSynonymsAndHypernyms.addAll(Utils.getHypernyms(subColumnName, POS.NOUN, dict));
									
									
								
									for(String colSynonymOrHypernym : colSynonymsAndHypernyms){
										//check all opportunities to find matches with this synonym-hypernym
										if((resultOpportunity = Utils.opportunityMatch(colSynonymOrHypernym, InAppAttributeExtractor.opportunities)) != null){
											FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), resultOpportunity, subName);
											foundOpportunity.setXMLdata(XMLParser.element_name, attr_name_value, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE);
											found_attributes.add(foundOpportunity);
										}
									}
								}
							}
							
						}
					}
			 
				}
			 
				public void endElement(String uri, String localName,
					String qName) throws SAXException {
			 
					//System.out.println("End Element :" + qName);
			 
				}
			 
				public void characters(char ch[], int start, int length) throws SAXException {
					/*
					String val = new String(ch, start, length);
					
					
					if(!val.isEmpty() && val.trim().length() > 0){
						// Split name if necessary to multiple names 
						ArrayList<String> subNames = Utils.cleanText(val);
						
						
						for(String subName : subNames){
							System.out.println("Element : " + XMLParser.element_name + "Element original content : " + val + ", Element sub-content :" + subName);
							
							String resultOpportunity = "";
							if((InAppAttributeExtractor.opportunities.containsKey(subName))){
								FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), subName, subName);
								foundOpportunity.setXMLdata(XMLParser.element_name, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE, val);
								found_attributes.add(foundOpportunity);
							}
							else{
								//check synonyms
								ArrayList<String> colSynonymsAndHypernyms = Utils.getSynonyms(subName, POS.NOUN, dict);
								//ArrayList<String> colSynonymsAndHypernyms = Utils.getHypernyms(subColumnName, POS.NOUN, dict);
								//colSynonymsAndHypernyms.addAll(Utils.getHypernyms(subColumnName, POS.NOUN, dict));
								
								
							
								for(String colSynonymOrHypernym : colSynonymsAndHypernyms){
									//check all opportunities to find matches with this synonym-hypernym
									if((resultOpportunity = Utils.opportunityMatch(colSynonymOrHypernym, InAppAttributeExtractor.opportunities)) != null){
										FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), resultOpportunity, subName);
										foundOpportunity.setXMLdata(XMLParser.element_name, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE, val);
										found_attributes.add(foundOpportunity);
									}
								}
							}
						}
					}
					*/
				}
		 
		    };
		 
		    saxParser.parse(file, handler);
		 
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return found_attributes;
	}
	
	
	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	/*********************************************************************************************/
	
	/**
	 * 
	 * @param file
	 * @param opportunitiesSynHyperHyponyms
	 * @return
	 */
	public static ArrayList<FoundOpportunity> parse(final File file,
			final HashMap<String, Opportunity> opportunitiesSynHyperHyponyms) {
		
		String filename = file.getName().substring(0, file.getName().lastIndexOf('.'));
		//InAppAttributeExtractor.logger.debug("Parsing xml file " + filename);
		
		final ArrayList<FoundOpportunity> found_attributes = new ArrayList<FoundOpportunity>();
		
		
		try {
			 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		 
			DefaultHandler handler = new DefaultHandler() {
			 
				public void startElement(String uri, String localName,String qName, 
			                Attributes attributes) throws SAXException {
			 
					XMLParser.element_name = qName;
					
					/* Loop through the element's attributes */
					for(int i = 0; i < attributes.getLength(); i++){
						String attr_name = attributes.getLocalName(i);
						String attr_name_value = attributes.getValue(i);
						
						//System.out.println("Element :" + qName + ", Attribute: " + attr_name + ", Attribute value: " + attr_name_value);
						
						if(attr_name.compareTo("name") == 0){
							/* Split name if necessary to multiple names */
							ArrayList<String> subNames = Utils.getSubNames(attr_name_value);
							
							
							for(String subName : subNames){
								//String resultOpportunity = Utils.opportunityKeyMatch(subName, opportunitiesSynHyperHyponyms);
								boolean match = Utils.opportunityKeyMatch2(subName, opportunitiesSynHyperHyponyms);;
								if(match &&
										isXMLAttrOK(subName, XMLParser.element_name, file.getName(), opportunitiesSynHyperHyponyms, subName)){
									FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), subName, subName);
									foundOpportunity.setXMLdata(XMLParser.element_name, attr_name_value, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE);
									found_attributes.add(foundOpportunity);
								}
								else{
									
									//check the synonyms, hypernyms and hyponyms of all ops
									for(String op : opportunitiesSynHyperHyponyms.keySet()){
										if(Utils.opportunitySynMatch(opportunitiesSynHyperHyponyms.get(op), subName) &&
												isXMLAttrOK(subName, XMLParser.element_name, file.getName(), opportunitiesSynHyperHyponyms, op)){
											//match with this op's syn, hyper or hypo
											FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), op, subName);
											foundOpportunity.setXMLdata(XMLParser.element_name, attr_name_value, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE);
											found_attributes.add(foundOpportunity);
										}
									}
								}
							}
							
						}
					}
			 
				}
			 
				public void endElement(String uri, String localName,
					String qName) throws SAXException {
			 
					//System.out.println("End Element :" + qName);
			 
				}
			 
				public void characters(char ch[], int start, int length) throws SAXException {
					/*
					String val = new String(ch, start, length);
					
					
					if(!val.isEmpty() && val.trim().length() > 0){
						// Split name if necessary to multiple names 
						ArrayList<String> subNames = Utils.cleanText(val);
						
						
						for(String subName : subNames){
							System.out.println("Element : " + XMLParser.element_name + "Element original content : " + val + ", Element sub-content :" + subName);
							
							String resultOpportunity = "";
							if((InAppAttributeExtractor.opportunities.containsKey(subName))){
								FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), subName, subName);
								foundOpportunity.setXMLdata(XMLParser.element_name, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE, val);
								found_attributes.add(foundOpportunity);
							}
							else{
								//check synonyms
								ArrayList<String> colSynonymsAndHypernyms = Utils.getSynonyms(subName, POS.NOUN, dict);
								//ArrayList<String> colSynonymsAndHypernyms = Utils.getHypernyms(subColumnName, POS.NOUN, dict);
								//colSynonymsAndHypernyms.addAll(Utils.getHypernyms(subColumnName, POS.NOUN, dict));
								
								
							
								for(String colSynonymOrHypernym : colSynonymsAndHypernyms){
									//check all opportunities to find matches with this synonym-hypernym
									if((resultOpportunity = Utils.opportunityMatch(colSynonymOrHypernym, InAppAttributeExtractor.opportunities)) != null){
										FoundOpportunity foundOpportunity = new FoundOpportunity(FoundOpportunity.XML, file.getName(), resultOpportunity, subName);
										foundOpportunity.setXMLdata(XMLParser.element_name, Preferences.DEFAULT_VALUE, Preferences.DEFAULT_VALUE, val);
										found_attributes.add(foundOpportunity);
									}
								}
							}
						}
					}
					*/
				}
		 
		    };
		 
		    saxParser.parse(file, handler);
		 
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		
		return found_attributes;
	}
	
	/**
	 * 
	 * @param attrName
	 * @param elName
	 * @param fileName
	 * @param opportunitiesSynHyperHyponyms
	 * @param opportunityName
	 * @return
	 */
	private static boolean isXMLAttrOK(String attrName, String elName,
			String fileName,
			HashMap<String, Opportunity> opportunitiesSynHyperHyponyms,
			String opportunityName) {
		
		String opType = opportunitiesSynHyperHyponyms.get(opportunityName).type;
		if(opType.compareTo(Opportunity.ATTRIBUTE) == 0){
			//System.out.println(opportunityName);
			HashSet<String> opDomains = opportunitiesSynHyperHyponyms.get(opportunityName).domains;
	//		String domain = "";
			
	//		for(String d : opDomains){
	//			domain = d;
	//			break;
	//		}
			
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			//ArrayList<String> fnames = Utils.getPackageSubNames(fileName);
			ArrayList<String> fnames = Utils.getWords(fileName);
			double sim = SimilarityCalculator.calculateMaxSim(fnames, opDomains, opType);
	
			//double sim = SimilarityCalculator.calculateResnik(fileName.trim().toLowerCase(), domain.trim().toLowerCase());
			//if(sim != Preferences.SIMILARITY_DEFAULT && sim > Preferences.LCH_THRESHOLD){
			if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LCH_ATTRIBUTE_THRESHOLD){
				System.out.println("ACCEPTED! name =" + attrName + ", fileName = " + fileName + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return true;
			}
			else{
				System.out.println("REJECTED! name =" + attrName + ", fileName = " + fileName + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return false;
			}
		
		}
		else{
			
			//opportunity is interest, check similarity with category
			ArrayList<String> cat_names = Utils.getCatSubNames(MyParser.currentCategory);
			//double sim = SimilarityCalculator.calculateMaxSim(cat_names, opportunityName, opType);
			double sim = SimilarityCalculator.getWeightedLesk(MyParser.currentCategory, opportunityName);
			
			if(sim > Preferences.SIMILARITY_DEFAULT && sim > Preferences.LESK_INTEREST_THRESHOLD){
				System.out.println("ACCEPTED! attrName =" + attrName + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return true;
			}
			else{
				System.out.println("REJECTED! attrName =" + attrName + ", category = " + MyParser.currentCategory + ", opportunity = " + opportunityName 
						+ "opType = " + opType + ", sim = " + sim);
				return false;
			}
		}
			
	}

}
