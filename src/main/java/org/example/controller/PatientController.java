package org.example.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.example.dao.PatientDAO;
import org.example.dto.PatientsDto;
import org.example.dto.PatientsFilterDto;
import org.example.exceptions.DataNotFoundException;
import org.example.mappers.PatientMapper;
import org.example.models.Patients;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/PATIENTS")
public class PatientController {

        PatientDAO dao = new PatientDAO();
    @Context UriInfo uriInfo;
    @Context HttpHeaders headers;

        @GET
        @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,"text/csv"})
        public Response getAllPatient(
                @BeanParam PatientsFilterDto filter
        ) {

            try {
                GenericEntity<ArrayList<Patients>> pats = new GenericEntity<ArrayList<Patients>>(dao.selectAllPats(filter)) {};
                if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf(MediaType.APPLICATION_XML))) {
                    return Response
                            .ok(pats)
                            .type(MediaType.APPLICATION_XML)
                            .build();

                  }
                else if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf("text/csv"))) {
                    return Response
                            .ok(pats)
                            .type("text/csv")
                            .build();
                }

                return Response
                        .ok(pats, MediaType.APPLICATION_JSON)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT")
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @GET
        @Path("{patientId}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "text/csv"} )
        public Response getPatient(
                @PathParam("patientId") int patientId) throws SQLException{

            try {
                Patients pats = dao.selectPat(patientId);


                if (pats == null) {
                    throw new DataNotFoundException("Patient " + patientId + " Not found");

                }

                if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf(MediaType.APPLICATION_XML))) {
                    return Response
                            .ok(pats)
                            .type(MediaType.APPLICATION_XML)
                            .build();
                }
                PatientsDto dto = PatientMapper.INSTANCE.toPatientDto(pats);

                addLinks(dto);

                return Response.ok(dto).build();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }



    private void addLinks (PatientsDto dto){
        URI selfUri = uriInfo.getAbsolutePath();
        URI empsUri = uriInfo.getAbsolutePathBuilder().path(PatientController.class).build();

        dto.addLink(selfUri.toString(), "self");
        dto.addLink(empsUri.toString(),"Patients");
    }


        @POST
        @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
        public Response insertPatient(Patients pats) {

            try {
                dao.insertPat(pats);
                NewCookie cookie = (new NewCookie.Builder("username")).value("00000").build();
                URI uri = uriInfo.getAbsolutePathBuilder().path(pats.getPatientId()+"").build();
                return Response
                        .created(uri)
                        .cookie(cookie)
                        .header("Created by", "Ragad Alghanim")
                        .build();
               // dao.insertEmp(emps);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @PUT
        @Path("{patientId}")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "text/csv"})
        public Response updatePatient(
                @PathParam("patientId") int patientId, Patients pats) {

            try {
                pats.setPatientId(patientId);
                dao.updatePat(pats);
                if(headers.getAcceptableMediaTypes().contains(MediaType.valueOf(MediaType.APPLICATION_XML))) {
                    return Response
                            .ok(pats)
                            .type(MediaType.APPLICATION_XML)
                            .build();
//
                }
                return Response.ok(pats).build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }



}
