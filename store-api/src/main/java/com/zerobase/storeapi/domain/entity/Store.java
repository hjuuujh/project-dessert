package com.zerobase.storeapi.domain.entity;

import com.zerobase.storeapi.domain.BaseEntity;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@AuditOverride(forClass = BaseEntity.class)
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId; // 매장 소유 파트너 id

    private String name; // 매장명
    private String description; // 매장 설명
    private String thumbnailUrl;

    @ColumnDefault("0")
    private int followCount;

    private LocalDate deletedAt; // 매장 삭제 정보

    public static Store of(Long sellerId, RegisterStore form) {
        return Store.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .thumbnailUrl(form.getThumbnailUrl())
                .build();
    }

    public void update(UpdateStore form) {
        this.name = form.getName();
        this.description = form.getDescription();
        this.thumbnailUrl = form.getThumbnailUrl();
    }

    public void delete() {
        this.deletedAt = LocalDate.now();
    }

    public void increaseFollow() {
        followCount++;
    }

    public void decreaseFollow() {
        this.followCount = Math.max(followCount - 1, 0);
    }
}
