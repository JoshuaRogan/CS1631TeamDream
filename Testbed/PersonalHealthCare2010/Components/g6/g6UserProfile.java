class UserProfile
{
	public String UserName;
	public String Sex;
	public int Age;
	public int Weight;
	public int Height;
	public String Diabetes;
	public String HeartDisease;
	public String Meal;
	public boolean hasYear;
	public boolean hasPrecondition;
	public boolean hasReadingType;

	UserProfile()
	{
		hasYear = false;
		hasPrecondition = false;
		hasReadingType = false;
	}

	UserProfile(KeyValueList theKeyValueList)
	{
		UserName = theKeyValueList.getValue("UserName");
		Sex = theKeyValueList.getValue("Sex");
		Age = Integer.parseInt(theKeyValueList.getValue("Age"));
		Weight = Integer.parseInt(theKeyValueList.getValue("Weight"));
		Height = Integer.parseInt(theKeyValueList.getValue("Height"));
		Diabetes = theKeyValueList.getValue("Diabetes");
		HeartDisease = theKeyValueList.getValue("HeartDisease");
		Meal = theKeyValueList.getValue("Meal");
		
		hasYear = true;
		hasPrecondition = true;
		hasReadingType = true;
	}
	
	public int getYear()
	{
		return Age;
	}
	
	public String getPrecondition()
	{
		if (Diabetes.equals("prediabetic"))
		{
			return "Pre-Diabetic";
		}
		else
		{
			return Diabetes.substring(0, 1).toUpperCase()+Diabetes.substring(1);
		}
	}
	
	public String getReadingType()
	{
		if (Meal.equals("after"))
		{
			return "Bedtime/overnight";
		}
		else
		{
			return "Before Meals";
		}
	}
	
	public String toString()
	{
		return ""+getYear()+"\t"+getPrecondition()+"\t"+getReadingType();
	}
}
