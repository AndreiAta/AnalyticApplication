package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.entity;

public class Site
{
    Integer id;
    String siteName;
    int visits;

    public Site(Integer id, String siteName, int visits)
    {
        this.id = id;
        this.siteName = siteName;
        this.visits = visits;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    public int getVisits()
    {
        return visits;
    }

    public void setVisits(int visits)
    {
        this.visits = visits;
    }
}
