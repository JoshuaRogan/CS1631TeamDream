import java.util.*;

public class BloodSugarKnowledgeList
{
	private ArrayList<BloodSugarKnowledge> List;
	
	BloodSugarKnowledgeList()
	{
		List = new ArrayList<BloodSugarKnowledge>();
	}
	
	public void addBloodSugarKnowledgeFrom(KeyValueList kvList)
	{
		for (int i=0;i<List.size();i++)
		{
			if (List.get(i).equals(kvList))
			{
				List.remove(i);
				break;
			}
		}
				
		List.add(new BloodSugarKnowledge(kvList));
	}
	
	public String getBloodSugarKnowledgeFrom(KeyValueList kvList)
	{
		for (int i=0;i<List.size();i++)
		{
			if (List.get(i).equals(kvList))
			{
				return List.get(i).getDiagnosis();
			}
		}
		
		return "No";
	}
}
