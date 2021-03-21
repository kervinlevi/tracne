package tech.codevil.tracne.common.util

/**
 * Created by kervin.decena on 21/03/2021.
 */
interface EntityMapper<Entity, DomainModel> {

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity

}