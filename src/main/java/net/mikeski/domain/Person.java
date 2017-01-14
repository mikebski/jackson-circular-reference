package net.mikeski.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 1/13/17.
 */
public class Person {
    private final Long id;
    private final String name;
    private List<Group> groups;

    public Person(Long id, String name){
        this.id = id;
        this.name = name;
        groups = new ArrayList<Group>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group){
        if(!this.groups.contains(group)){
            groups.add(group);
            group.addPerson(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!id.equals(person.id)) return false;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
