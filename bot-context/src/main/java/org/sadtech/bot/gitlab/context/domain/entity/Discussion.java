package org.sadtech.bot.gitlab.context.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.haiti.context.domain.BasicEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Getter
@Setter
@Entity
@Table(name = "discussion")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Discussion implements BasicEntity<String> {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "responsible_id")
    private Person responsible;

    @ManyToOne()
    @JoinTable(
            name = "discussion_merge_request",
            joinColumns = @JoinColumn(name = "discussion_id"),
            inverseJoinColumns = @JoinColumn(name = "merge_request_id")
    )
    private MergeRequest mergeRequest;

    @OneToMany(
            mappedBy = "discussion",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    private List<Note> notes;

    public void setNotes(List<Note> notes) {
        notes.forEach(note -> note.setDiscussion(this));
        this.notes = notes;
    }

}
