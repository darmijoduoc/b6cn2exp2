package com.biblioteca.graphql;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.*;

public final class GraphQLProvider {

    private GraphQLProvider() {}

    public static GraphQL buildGraphQL() {

        QueryResolver queryResolver = new QueryResolver();
        MutationResolver mutationResolver = new MutationResolver();

        GraphQLObjectType prestamoType = GraphQLObjectType.newObject()
            .name("Prestamo")
            .field(f -> f.name("id").type(Scalars.GraphQLID))
            .field(f -> f.name("usuarioId").type(Scalars.GraphQLID))
            .field(f -> f.name("libroId").type(Scalars.GraphQLID))
            .field(f -> f.name("fechaPrestamo").type(Scalars.GraphQLString))
            .field(f -> f.name("fechaDevolucion").type(Scalars.GraphQLString))
            .field(f -> f.name("estado").type(Scalars.GraphQLString))
            .build();

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
            .name("Query")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("getPrestamos")
                .type(GraphQLList.list(prestamoType))
                .dataFetcher(env -> queryResolver.getPrestamos()))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("getPrestamoById")
                .type(prestamoType)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .dataFetcher(env -> queryResolver.getPrestamoById(env.getArgument("id"))))
            .build();

        GraphQLObjectType mutationType = GraphQLObjectType.newObject()
            .name("Mutation")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("createPrestamo")
                .type(prestamoType)
                .argument(GraphQLArgument.newArgument().name("usuarioId").type(Scalars.GraphQLID))
                .argument(GraphQLArgument.newArgument().name("libroId").type(Scalars.GraphQLID))
                .dataFetcher(env -> mutationResolver.createPrestamo(
                    env.getArgument("usuarioId"),
                    env.getArgument("libroId"))))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("updatePrestamo")
                .type(prestamoType)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .argument(GraphQLArgument.newArgument().name("estado").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("fechaDevolucion").type(Scalars.GraphQLString))
                .dataFetcher(env -> mutationResolver.updatePrestamo(
                    env.getArgument("id"),
                    env.getArgument("estado"),
                    env.getArgument("fechaDevolucion"))))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("deletePrestamo")
                .type(Scalars.GraphQLBoolean)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .dataFetcher(env -> mutationResolver.deletePrestamo(env.getArgument("id"))))
            .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
            .query(queryType)
            .mutation(mutationType)
            .build();

        return GraphQL.newGraphQL(schema).build();
    }
}
