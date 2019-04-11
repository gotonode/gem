package io.github.gotonode.gem.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Link implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "site", nullable = false)
    private int site;

    @Column(name = "version", nullable = false)
    private int version;

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", used=" + used +
                ", date=" + date +
                ", site=" + site +
                ", version=" + version +
                '}';
    }
}
