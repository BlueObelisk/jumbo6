package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math.fraction.Fraction;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Util;

public class MorganMD5 {
	public static final String COUNT_START = "*";
	public static final String COUNT_END = "*";
	public final static String MD5PATTERN_S = "[0-9a-f]{25,33}";
	public final static Pattern MD5PATTERN = Pattern.compile(MD5PATTERN_S);
	public final static Pattern COUNTED_MD5PATTERN = Pattern.compile("\\"+COUNT_START+"(\\d+)"+"\\"+COUNT_END+"("+MD5PATTERN_S+")");
 
	private Integer count;
	private String equivalenceString;
	
	private MorganMD5(Integer count, String equivalenceString) {
		if (!MD5PATTERN.matcher(equivalenceString).matches()) {
			throw new RuntimeException("Cannot match as morganMD5 "+equivalenceString);
		}
		this.count = (count == null) ? new Integer(1) : count;
		this.equivalenceString = equivalenceString;
	}
	
	public String getCountedString() {
		return prefixMultiplier(count)+equivalenceString;
	}

	public Integer getCount() {
		return count;
	}

	public String getEquivalenceString() {
		return equivalenceString;
	}


	
	public static List<MorganMD5> getCountedMorganList(CMLMolecule molecule) {
		return getCountedMorganList(molecule, 1);
	}
	
	public static List<MorganMD5> getCountedMorganList(CMLMolecule molecule, int  multiplier) {
		List<CMLMolecule> childMolecules = CMLMolecule.getChildMoleculeList(molecule);
		List<String> morganStringList = new ArrayList<String>();
		for (CMLMolecule childMolecule : childMolecules) {
			morganStringList.add(createMorganMD5(childMolecule));
		}
		Collections.sort(morganStringList);
		List<MorganMD5> morganList = new ArrayList<MorganMD5>();
		String currentString = null;
		int count = 0;
		for (String childMorganString : morganStringList) {
			if (currentString == null) {
				count = 1;
				currentString = childMorganString;
			} else if (childMorganString.equals(currentString)) {
				count++;
			} else {
				MorganMD5 morganMd5 = new MorganMD5(count, currentString);
				morganList.add(morganMd5);
				count = 1;
				currentString = childMorganString;
			}
		}
		
		MorganMD5 morganMd5 = new MorganMD5(count, currentString);
		morganList.add(morganMd5);
		return morganList;
	}

	public static List<Fraction> getMultiplierList(String morganString1, String morganString2) {
		List<MorganMD5> morganList1 = MorganMD5.interpretCountedMorganString(morganString1);
		List<MorganMD5> morganList2 = MorganMD5.interpretCountedMorganString(morganString2);
		return getMultiplierList(morganList1, morganList2);
	}
	

	public static List<Fraction> getMultiplierList(
			List<MorganMD5> morganList1, List<MorganMD5> morganList2) {
		List<Fraction> multiplierList = null;
		if (morganList1.size() == morganList2.size()) {
			multiplierList = new ArrayList<Fraction>();
			int serial = 0;
			for (MorganMD5 morgan1 : morganList1) {
				MorganMD5 morgan2 = morganList2.get(serial++);
				Fraction multiplier = morgan1.getRatioOfCountToCount2(morgan2);
				multiplierList.add(multiplier);
			}
		}
		
		return multiplierList;
	}

	public Fraction getRatioOfCountToCount2(MorganMD5 morgan2) {
		Fraction fraction = null;
		if (this.getEquivalenceString().equals(morgan2.getEquivalenceString())) {
			fraction = new Fraction(this.getCount(), morgan2.getCount());
		}
		return fraction;
	}
	
	public static boolean haveIdenticalComponents(String morganString1, String morganString2) {
		List<Fraction> multiplierList = getMultiplierList(morganString1, morganString2);
		if (multiplierList == null) {
			return false;
		}
		for (Fraction fraction : multiplierList) {
			if (fraction == null) {
				return false; 
			}
		}
		return true;
	}

	public static Fraction getCommonMultiplier(String morganString1, String morganString2) {
		List<Fraction> multiplierList = getMultiplierList(morganString1, morganString2);

		Fraction commonFraction = null;
		if (multiplierList != null) {
			for (Fraction fraction : multiplierList) {
				if (fraction == null) {
					return null;
				} else if (commonFraction == null) {
					commonFraction = fraction;
				} else {
					if (!fraction.equals(commonFraction)) {
						return null;
					}
				}
			}
		}
		return commonFraction;
	}

	public static List<MorganMD5> interpretCountedMorganString(String s) {
		List<MorganMD5> countedList = new ArrayList<MorganMD5>();
		if (MD5PATTERN.matcher(s).matches()) {
			countedList.add(new MorganMD5(1, s));
		} else {
			int start = 0;
			Matcher matcher = COUNTED_MD5PATTERN.matcher(s);
			while (matcher.find(start)) {
				if (start != matcher.start()) {
					throw new RuntimeException("concatenated MD5 contains junk after:"+s.substring(start));
				}
				String countS = matcher.group(1);
				String morganString = matcher.group(2);
				countedList.add(new MorganMD5(new Integer(countS), morganString));
				start = matcher.end();
			}
			if (start == 0) {
				throw new RuntimeException("concatenated MD5 has leading junk (must start with count): "+s.substring(start));
			}
			if (start != s.length()) {
				throw new RuntimeException("concatenated MD5 has trailing junk: "+s.substring(start));
			}
		}
		return countedList;
	}
	
	public static String createMorganMD5(CMLMolecule molecule) {
		return createMorganMD5(molecule, 1);
	}	
	
	public static String createMorganMD5(CMLMolecule molecule, int multiplier) {
		String morganString = null;
		if (molecule != null) {
			if (molecule.getMoleculeCount() > 0) {
				List<MorganMD5> morganList = MorganMD5.getCountedMorganList(molecule, multiplier);
				morganString = MorganMD5.createMorganString(morganList);
			} else {
				Morgan morgan = new Morgan(molecule);
				morganString = Util.calculateMD5(morgan.getEquivalenceString());
				if (multiplier != 1) {
					morganString = prefixMultiplier(multiplier)+morganString;
				}
			}
		}
		return morganString;
	}


	private static String prefixMultiplier(int multiplier) {
		return COUNT_START+multiplier+COUNT_END;
	}

	private static String createMorganString(List<MorganMD5> morganList) {
		StringBuilder sb = new StringBuilder();
		for (MorganMD5 morganMD5 : morganList) {
			sb.append(morganMD5.getCountedString());
		}
		return sb.toString();
	}

}
