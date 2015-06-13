package cz.pragueparking.dataloader;

public class AutomatEdge {
    public AutomatVertex source;
    public AutomatVertex target;

    public AutomatEdge(AutomatVertex source, AutomatVertex target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatEdge that = (AutomatEdge) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return !(target != null ? !target.equals(that.target) : that.target != null);

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AutomatEdge{" + source + " ->" + target + '}';
    }
}
