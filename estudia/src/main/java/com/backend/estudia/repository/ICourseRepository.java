package com.backend.estudia.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.estudia.entity.Course;

public interface ICourseRepository extends JpaRepository<Course, Long> {
	
	public Optional<Course> findByName(String name);
	public Optional<Course> findDistinctById(Long id);
	public Page<Course> findByIdIn(Pageable pageable, Collection ids);
	//Filtrado por todos los campos y sin ordenar
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCase(Pageable pageable, String nameFilter, String areaFilter, String descriptionFilter);
	//Filtrado por todos los campos y ordenado por nombre
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByName(Pageable pageable, String nameFilter, String areaFilter, String descriptionFilter);
	//Filtrado por todos los campos y ordenado por area
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByArea(Pageable pageable, String nameFilter, String areaFilter, String descriptionFilter);
	//Filtrado por todos los campos y ordenado por description
	public Page<Course> findByNameIgnoreCaseContainingAndAreaIgnoreCaseContainingAndDescriptionIgnoreCaseContainingOrderByDescription(Pageable pageable, String nameFilter, String areaFilter, String descriptionFilter);
	//Filtrado por los campos name y area y sin ordenar
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCase(Pageable pageable, String nameFilter, String areaFilter);
	//Filtrado por los campos name y area y ordenado por nombre
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseOrderByName(Pageable pageable, String nameFilter, String areaFilter);
	//Filtrado por los campos name y area y ordenado por area
	public Page<Course> findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseOrderByArea(Pageable pageable, String nameFilter, String areaFilter);
	//Filtrado por los campos name y descripcion y sin ordenar
	public Page<Course> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(Pageable pageable, String nameFilter, String descriptionFilter);
	//Filtrado por los campos name y descripcion y ordenado por nombre
	public Page<Course> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByName(Pageable pageable, String nameFilter, String descriptionFilter);
	//Filtrado por los campos name y descripcion y ordenado por descripcion
	public Page<Course> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByDescription(Pageable pageable, String nameFilter, String descriptionFilter);
	//Filtrado por los campos area y descripcion y sin ordenar
	public Page<Course> findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCase(Pageable pageable, String areaFilter, String descriptionFilter);
	//Filtrado por los campos area y descripcion y ordenado por area
	public Page<Course> findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByArea(Pageable pageable, String areaFilter, String descriptionFilter);
	//Filtrado por los campos area y descripcion y ordenado por descripcion
	public Page<Course> findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByDescription(Pageable pageable, String areaFilter, String descriptionFilter);
	//Filtrado por coincidencia en cualquiera de los param
	public Page<Course> findByNameContainingIgnoreCaseOrAreaContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Pageable pageable, String nameFilter, String areaFilter, String descriptionFilter);
	//Filtrado por descripcion sin ordenar
	public Page<Course> findByDescriptionContainingIgnoreCase(Pageable pageable, String descriptionFilter);
	//Filtrado por descripcion y ordenado por descripc
	public Page<Course> findByDescriptionContainingIgnoreCaseOrderByDescription(Pageable pageable, String descriptionFilter);
	//Filtrado por area sin ordenar
	public Page<Course> findByAreaContainingIgnoreCase(Pageable pageable, String areaFilter);
	//Filtrado por area y ordenado por area
	public Page<Course> findByAreaContainingIgnoreCaseOrderByArea(Pageable pageable, String areaFilter);
	//Filtrado por nombre sin ordenar
	public Page<Course> findByNameContainingIgnoreCase(Pageable pageable, String nameFilter);
	//Filtrado por nombre y ordenado por nombre
	public Page<Course> findByNameContainingIgnoreCaseOrderByName(Pageable pageable, String nameFilter);
	//Order by name
	public Page<Course> findAllByOrderByName(Pageable pageable);
	//Order by area
	public Page<Course> findAllByOrderByArea(Pageable pageable);
	//Order by description
	public Page<Course> findAllByOrderByDescription(Pageable pageable);
	
	@Modifying
    @Query("DELETE FROM Course WHERE id=:id")
    void customDeleteCourseById(@Param("id") Long id);
	
}
