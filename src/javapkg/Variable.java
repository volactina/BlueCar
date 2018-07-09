package javapkg;

public
class Variable {

	private String name;
  	private int type;
  	private String val;
  	private int scope;

	public Variable(String name) {
		this.name = name;
	}

	public Variable(String name, int type, int scope) {
    	this.name = name;
        this.type = type;
        this.val = null;
        this.scope = scope;
    }

    public String getVal() {
      return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
    
    public int getType() {
      return type;
    }

    public void setType(int type) {
        this.type=type;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

	public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Variable variable = (Variable) object;
        return java.util.Objects.equals(val, variable.val);
    }

    public int hashCode() {

        return java.util.Objects.hash(super.hashCode(), val);
    }
}
/* JavaCC - OriginalChecksum=9fc08c3506a52246a3db0d2d8a50da30 (do not edit this line) */
