package dev.struchkov.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Optional;

/**
 * @author upagge 11.02.2021
 */
@Getter
@Setter
@Entity
@Table(name = "discussion")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Discussion {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "responsible_id")
    private Person responsible;

    @Column(name = "resolved")
    private Boolean resolved;

    @ManyToOne
    @JoinTable(
            name = "discussion_merge_request",
            joinColumns = @JoinColumn(name = "discussion_id"),
            inverseJoinColumns = @JoinColumn(name = "merge_request_id")
    )
    private MergeRequestForDiscussion mergeRequest;

    @OneToMany(
            mappedBy = "discussion",
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH
            }
    )
    private List<Note> notes;

    public void setNotes(List<Note> notes) {
        notes.forEach(note -> note.setDiscussion(this));
        this.notes = notes;
    }

    public Note getFirstNote() {
        return this.notes.get(0);
    }

    public Optional<Note> getPrevLastNote() {
        final int size = notes.size();
        if (size > 2) {
            return Optional.of(notes.get(size - 2));
        }
        return Optional.empty();
    }


}
