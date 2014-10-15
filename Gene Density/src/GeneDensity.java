import java.util.ArrayList;

/*
 * 1 class per isoform (cut off everything after dash)
store list of isoform in arraylist

Class Name: isoform
Class Name: record
column 3: feature
column 4: start
column 5: stop
column 7: strand (boolean)
column 10: name (use function to clean "" and ""; && everything after dash)
 * 
 */

public class GeneDensity {
   ArrayList<Isoform> isoforms = new ArrayList<Isoform>(); //place isoform objects here
   ArrayList<DNAFrag> nests = new ArrayList<DNAFrag>(); // place discovered nests here
   int length, numA, numT, numG, numC, numGC, numN;
   int nestedCount=0, nestedLength=0, nestedA, nestedT, nestedG, nestedC, nestedGC, nestedN;
   int igCount=0, igLength=0, igA, igT, igG, igC, igGC, igN;
   boolean isoformSorted = false;
   String DNA;

   // put this into Record's genData function to get statistical calculations
   public static String extractRecordDNA(String dna, Record rec) {
      return rec.direction ? dna.substring(rec.start,rec.stop) : reverseComplement(dna.substring(rec.stop,rec.start));
   }
   
   // this is here just because of the other ones. gotta keep things looking organized
   public static String extractFragDNA(String dna, DNAFrag frag) {
      return dna.substring(frag.start, frag.stop);
   }
   
   // not sure what this should be used for yet
   public static String extractGeneDNA(String dna, Isoform iso) {
      return iso.direction ? dna.substring(iso.start, iso.stop) : reverseComplement(dna.substring(iso.stop, iso.start));
   }
   
   // gets the reverse complement of a dna string. useful when the gene is written reversed
   public static String reverseComplement(String dna) {
      char[] reverse = new StringBuilder(dna).reverse().toString().toCharArray();
      for (int i = 0; i<reverse.length; i++) {
         if (reverse[i] == 'A')
            reverse[i] = 'T';
         else if (reverse[i] == 'T')
            reverse[i] = 'A';
         else if (reverse[i] == 'G')
            reverse[i] = 'C';
         else if (reverse[i] == 'C')
            reverse[i] = 'G';
      }
      return reverse.toString();
   }
   
   /* return values: 0: no nesting
    *                1: nested from iso1.start to iso2.stop
    *                2: nested from iso2.start to iso1.start
    *                3: iso1 is nested inside iso2
    *                4: nested from iso1.stop to iso2.stop
    *                5: nested from iso2.start to iso1.stop
    *                6: iso1 is nested inside iso2
    *                7: nested from iso2.start to iso1.stop
    *                8: nested from iso1.start to iso2.start
    *                9: iso2 is nested inside iso1
    *               10: nested from iso2.stop to iso1.stop
    *               11: nested from iso1.start to iso2.stop
    *               12: iso2 is nested inside iso1
    */
   public int isNested(Isoform iso1, Isoform iso2) {
      if (iso1.start > iso2.start && iso1.start < iso2.stop)
         if (iso1.stop > iso2.stop)                   // nested from iso1.start to iso2.stop
            return 1;
         else if (iso1.stop < iso2.start)             // nested from iso2.start to iso1.start
            return 2;
         else                                         // iso1 is nested inside iso2 <-- something went wrong?
            return 3;
      else if (iso1.stop > iso2.start && iso1.stop < iso2.stop)
         if (iso1.start > iso2.stop)                  // nested from iso1.stop to iso2.stop
            return 4;
         else if (iso1.start < iso2.start)            // nested from iso2.start to iso1.stop
            return 5;
         else                                         // iso1 is nested inside iso2 <-- something went wrong?
            return 6;
      else if (iso2.start > iso1.start && iso2.start < iso1.stop)
         if (iso2.stop > iso1.stop)                   // nested from iso2.start to iso1.stop
            return 7;
         else if (iso2.stop < iso1.start)             // nested from iso1.start to iso2.start
            return 8;
         else                                         // iso2 is nested inside iso1 <-- something went wrong?
            return 9;
      else if (iso2.stop > iso1.start && iso2.stop < iso1.stop)
         if (iso2.start > iso1.stop)                  // nested from iso2.stop to iso1.stop
            return 10;
         else if (iso2.start < iso1.start)            // nested from iso1.start to iso2.stop
            return 11;
         else                                         // iso2 is nested inside iso1 <-- something went wrong?
            return 12;
      else
         return 0;
   }
   
   // hint should be return value from isNested
   // return is [start,stop]
   public int[] nested(Isoform iso1, Isoform iso2, int hint) {
      int[] range;
      if (hint == 1)
         range = new int[] {iso1.start,iso2.stop};
      else if (hint == 2)
         range = new int[] {iso2.start,iso1.start};
      else if (hint == 4)
         range = new int[] {iso1.stop,iso2.stop};
      else if (hint == 5)
         range = new int[] {iso2.start,iso1.stop};
      else if (hint == 3 || hint == 6)
         range = (iso1.direction) ? new int[] {iso1.start,iso1.stop} : new int[] {iso1.stop, iso1.start};
      else if (hint == 7)
         range = new int[] {iso2.start,iso1.stop};
      else if (hint == 8)
         range = new int[] {iso1.start,iso2.start};
      else if (hint == 10)
         range = new int[] {iso2.stop,iso1.stop};
      else if (hint == 11)
         range = new int[] {iso1.start,iso2.stop};
      else if (hint == 9 || hint == 12)
         range = (iso2.direction) ? new int[] {iso2.start,iso2.stop} : new int[] {iso2.stop, iso2.start};
      else
         range = new int[] {0,0};
         
      return range;
   }
   
   // do an n^2 comparison of isoforms and find nested regions and store it as a DNAFrag in nests
   // finds and calculates Nests/data
   public void calcNests() {
      // for each nested region, create a DNAFrag object, calculate the data, and then store it in the array list
      for (int i = 0; i < isoforms.size()-1; i++) {
         for (int n = 1; n < isoforms.size(); n++) {
            int hint = isNested(isoforms.get(i), isoforms.get(n));
            if (hint != 0) {
               int[] startstop = nested(isoforms.get(i), isoforms.get(n), hint);
               DNAFrag frag = new DNAFrag(startstop[0], startstop[1]);
               frag.genData(extractFragDNA(DNA, frag));
               nests.add(frag);
            }   
         }
      }
   }
   
   // we can't generate nested and intergenic at the same time in the /veryveryvery/ rare case where we have multiple nests involving the same few genes
   public void calcIntergenic() {
      // need to sort Isoforms from lowest to highest and look at the gaps between neighbors.
      // this function is going to pretend that overlaps don't happen, so stop is between another's start and stop, we ignore it.
      // ignore the previous line....if there is an overlap, we would be able to fix that by adding in the nested regions
      if (!isoformSorted)
         sortIsoforms();
      
      igLength = length;
      igA = numA;
      igT = numT;
      igG = numG;
      igC = numC;
      igGC = numGC;
      igN = numN;
      igCount = isoforms.size()+1; // there is a good chance that this logic is correct. if we really want to be accurate and test all cases, we should have two booleans in the main to verify start and stop location

      for(Records exon: records) {
         intronLength -= exon.length;
         intronA -= exon.numA;
         intronT -= exon.numT;
         intronG -= exon.numG;
         intronC -= exon.numC;
         intronGC -= exon.numGC;
         intronN -= exon.numN;
      }
      
   }
   
   public void sortIsoforms() {
      // some sorting happens here
      isoformSorted = true;
   }
   
   public void genData(String dna) {
      length = dna.length();
      numA = length - dna.replace("A", "").length();
      numT = length - dna.replace("T", "").length();
      numG = length - dna.replace("G", "").length();
      numC = length - dna.replace("C", "").length();
      numN = length - dna.replace("N", "").length();
      numGC = numG + numC;
   }
   
}

class Isoform {//implements Comparable{
   ArrayList<Record> records = new ArrayList<Record>();
   ArrayList<Record> exons = new ArrayList<Record>();
   int start;
   int stop;
   boolean direction, exonSorted = false;
   int length, numA, numT, numG, numC, numGC, numN;
   int intronCount, intronLength, intronA, intronT, intronG, intronC, intronGC, intronN;
   int CDScount=0, CDSlength=0, exonCount=0, exonLength=0;
   
   public void setStart(int start) {
      this.start = start;
   }
   
   public void setStop(int stop) {
      this.stop = stop;
   }
   
   public void setDirection(boolean Direction) {
      this.direction = direction;
   }
   
   public void runAllDataCalc(String dna) {
      genData(dna);
      calcIntronData();
      genRecordStat();
   }
   
   public void calcIntronData() {
      if (!exonSorted)
         sortExons();
      
      intronLength = length;
      intronA = numA;
      intronT = numT;
      intronG = numG;
      intronC = numC;
      intronGC = numGC;
      intronN = numN;
      intronCount = exons.size()-1;

      for(Records exon: records) {
         intronLength -= exon.length;
         intronA -= exon.numA;
         intronT -= exon.numT;
         intronG -= exon.numG;
         intronC -= exon.numC;
         intronGC -= exon.numGC;
         intronN -= exon.numN;
      }
   }
   
   // data pertaining to the gene as a whole, not the Records
   // assume that the dna string is pre-parsed using extractIsoformDNA()
   public void genData(String dna) {
      length = dna.length();
      numA = length - dna.replace("A", "").length();
      numT = length - dna.replace("T", "").length();
      numG = length - dna.replace("G", "").length();
      numC = length - dna.replace("C", "").length();
      numN = length - dna.replace("N", "").length();
      numGC = numG + numC;
   }
   
   public void genRecordStat() {
      for (Record rec: records) {
         if (rec.type == 3) {
            CDScount++;
            CDSlength += rec.length;
         } else if (rec.type == 4) {
            exonCount++;
            exonLength += rec.length;
         }
      }
   }
   
   public void sortExons() {
      // some sorting happends here.
      // probably Collections.sort()
      
      exonSorted = true;
   }
}

class Record {//implements Comparable{
   String feature, isoform;
   int start, stop;
   boolean direction; // true = forward, false = backwards
   int type = 0; //0: no type, 1: start, 2: stop, 3: CDS, 4: exon
   int length, numA, numT, numG, numC, numGC, numN;
   
   public Record(String feature, int start, int stop, boolean direction, String isoform) {
      this.feature = feature;
      this.start = start;
      this.stop = stop;
      this.direction = direction;
      this.isoform = isoform;
      
      if (feature.equals("start_codon"))
         type = 1;
      else if (feature.equals("stop_codon"))
         type = 2;
      else if (feature.equals("CDS"))
         type = 3;
      else if (feature.equals("exon"))
         type = 4;
   }
   
   // assume that the dna string is pre-parsed using extractRecordDNA()
   // hopefully this is run before it becomes a part of an Isoform otherwise running is a pain
   public void genData(String dna) {
      length = dna.length();
      numA = length - dna.replace("A", "").length();
      numT = length - dna.replace("T", "").length();
      numG = length - dna.replace("G", "").length();
      numC = length - dna.replace("C", "").length();
      numN = length - dna.replace("N", "").length();
      numGC = numG + numC;
   }
}

class DNAFrag {
   int start, stop;
   int length, numA, numT, numG, numC, numGC, numN;
   String iso1, iso2; // this is where we put either the two isoforms that are nested or the isoforms surrounding the intergenic region
   
   public DNAFrag(int start, int stop) {
      this.start = start;
      this.stop = stop;
   }
   
   // assume that the dna string is pre-parsed using extractFragDNA()
   public void genData(String dna) {
      length = dna.length();
      numA = length - dna.replace("A", "").length();
      numT = length - dna.replace("T", "").length();
      numG = length - dna.replace("G", "").length();
      numC = length - dna.replace("C", "").length();
      numN = length - dna.replace("N", "").length();
      numGC = numG + numC;
   }
}
