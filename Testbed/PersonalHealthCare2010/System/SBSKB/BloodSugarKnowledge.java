public class BloodSugarKnowledge
{
	private String Sex;
	private int Age;
	private int Weight;
	private int Height;
	private String Diabetes;
	private String HeartDisease;
	private String Meal;
	private int BloodSugar;
	private String Diagnosis;
	
	BloodSugarKnowledge(KeyValueList theKeyValueList)
	{
		Sex = theKeyValueList.getValue("Sex");
		Age = Integer.parseInt(theKeyValueList.getValue("Age"));
		Weight = Integer.parseInt(theKeyValueList.getValue("Weight"));
		Height = Integer.parseInt(theKeyValueList.getValue("Height"));
		Diabetes = theKeyValueList.getValue("Diabetes");
		HeartDisease = theKeyValueList.getValue("Heart Disease");
		Meal = theKeyValueList.getValue("Meal");
		BloodSugar = Integer.parseInt(theKeyValueList.getValue("Blood Sugar"));
		Diagnosis = theKeyValueList.getValue("Diagnosis");
	}
	
	public boolean equals(KeyValueList theKeyValueList)
	{
		if (Sex.equals(theKeyValueList.getValue("Sex"))&&
			Age==Integer.parseInt(theKeyValueList.getValue("Age"))&&
			Weight==Integer.parseInt(theKeyValueList.getValue("Weight"))&&
			Height==Integer.parseInt(theKeyValueList.getValue("Height"))&&
			Diabetes.equals(theKeyValueList.getValue("Diabetes"))&&
			HeartDisease.equals(theKeyValueList.getValue("Heart Disease"))&&
			Meal.equals(theKeyValueList.getValue("Meal"))&&
			BloodSugar==Integer.parseInt(theKeyValueList.getValue("Blood Sugar")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getDiagnosis()
	{
		return Diagnosis;
	}
}
