package me.seoly.spring.jpa

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@MappedSuperclass
@Access(AccessType.FIELD)
abstract class BaseEntity: Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @ColumnDefault("CURRENT_TIMESTAMP")
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @ColumnDefault("CURRENT_TIMESTAMP")
    lateinit var updatedAt: LocalDateTime

}