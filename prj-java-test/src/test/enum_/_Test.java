package test.enum_;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;

public class _Test {
	
	enum EnumA {
		AAA("aaa"),
		BBB("bbb"),
		;
		private String desc;
		private EnumA(String desc) {
			this.desc = desc;
		}
		public String getDesc() {
			return this.desc;
		}
	}

	private static final Enum EnumA = null;
	
/*	public <e> void run(Enum<?> e) {
        for(Enum en: e.()){

            if(en.name().equalsIgnoreCase(s)){

          return s;
           }
       }
	}*/
	
	public <E extends Enum<E>> E run2(E enumToCheckForMatch) {
	//public <E extends Enum<E>> E run2(E enumToCheckForMatch) {	
        //for (E enumVal: enumToCheckForMatch.values())
        for (Enum<E> enumVal: enumToCheckForMatch.getClass().getEnumConstants())
        {
        	System.out.println(enumVal.name());
        	System.out.println(enumVal.toString());
        	System.out.println(enumVal.ordinal());
        	
        	
                /*if (enumVal.toString().equalsIgnoreCase(elementToCheck))
                {
                    //return enumVal;
                    return (E)enumVal;
                }*/
        }
        return null;
	}
	
	///////////////////////////
//	public enum CodeGroup {
//		Code1,Code2;
//		
		public interface EnumCodes<T> {
			T getCode();
			T getCodeName();
		}

	
		public  enum Codes1 implements EnumCodes<String> {
		    RUNNING("A1","a"),
		    STOPPED("B1","b"),
		    IDLE("C1","c");
	
			private final String code;
			private final String value;
	
		    private Codes1(String code,String value) {
		    	this.code = code;
		    	this.value = value;
		    }
			@Override
			public String getCode() {
				// TODO Auto-generated method stub
				return this.code;
			}
			@Override
			public String getCodeName() {
				// TODO Auto-generated method stub
				return this.value;
			}
		}
//	}	
	
/////////////////////////
	
	//public <e> void run4(Enum<?> e) {
	//public Enum<?> run4(Enum<?> e) {
	//public <E extends Enum<E>> E run4(E edd) {
	//public <E extends Enum<E>> E run4(E edd) {
	public <E extends Enum<E>> E[] run4(E[] edd) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException {		
		for(E code : edd) {	
			Class<?> clazz = code.getClass();
			Method method = clazz.getMethod("getCodeName");
			System.out.println("VV:"+clazz.getMethod("getCode").invoke(code)); // Prints "India"
			System.out.println("VV:"+clazz.getMethod("getCodeName").invoke(code)); // Prints "India"
		}
		return edd;
	}
	
	public <T> void run3(T ee) {
		
//		for (Object obj : ee.getClass().getEnumConstants()) {
//		    Class<?> clzz = obj.getClass();
//		    Method method = clzz.getDeclaredMethod("getDesc");
//		    String val = (String) method.invoke(obj);
//		    System.out.println("value : " + val); // prints SytemRunning, SystemStopped and tmpIdle
//
//		}
		
		/*//return null;
        for (Enum<T> enumVal: ee.getClass().getEnumConstants())
        {
        	System.out.println(enumVal.name());
        	System.out.println(enumVal.toString());
        	System.out.println(enumVal.ordinal());
        	
        	
                if (enumVal.toString().equalsIgnoreCase(elementToCheck))
                {
                    //return enumVal;
                    return (E)enumVal;
                }
        }*/
	}
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException {
		_Test test =  new _Test();
		//test.run2(EnumA.AAA);
		//test.run3(EnumA);
		
		test.run4(Codes1.values());
		
	}
}