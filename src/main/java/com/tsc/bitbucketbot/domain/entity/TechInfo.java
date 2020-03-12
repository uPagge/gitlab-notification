package com.tsc.bitbucketbot.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "tech_info")
@EqualsAndHashCode(of = "surogatId")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TechInfo {

    @Id
    @Column(name = "surogat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surogatId;

    @Column(name = "last_comment_id")
    private Long lastCommentId;

}
