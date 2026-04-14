package com.biblioteca.graphql;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.*;

public final class GraphQLProvider {

    private GraphQLProvider() {}

    public static GraphQL buildGraphQL() {

        QueryResolver queryResolver = new QueryResolver();
        MutationResolver mutationResolver = new MutationResolver();

        GraphQLObjectType libroType = GraphQLObjectType.newObject()
            .name("Libro")
            .field(f -> f.name("id").type(Scalars.GraphQLID))
            .field(f -> f.name("titulo").type(Scalars.GraphQLString))
            .field(f -> f.name("autor").type(Scalars.GraphQLString))
            .field(f -> f.name("isbn").type(Scalars.GraphQLString))
            .field(f -> f.name("disponible").type(Scalars.GraphQLBoolean))
            .build();

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
            .name("Query")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("getLibros")
                .type(GraphQLList.list(libroType))
                .dataFetcher(env -> queryResolver.getLibros()))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("getLibroById")
                .type(libroType)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .dataFetcher(env -> queryResolver.getLibroById(env.getArgument("id"))))
            .build();

        GraphQLObjectType mutationType = GraphQLObjectType.newObject()
            .name("Mutation")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("createLibro")
                .type(libroType)
                .argument(GraphQLArgument.newArgument().name("titulo").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("autor").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("isbn").type(Scalars.GraphQLString))
                .dataFetcher(env -> mutationResolver.createLibro(
                    env.getArgument("titulo"),
                    env.getArgument("autor"),
                    env.getArgument("isbn"))))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("updateLibro")
                .type(libroType)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .argument(GraphQLArgument.newArgument().name("titulo").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("autor").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("isbn").type(Scalars.GraphQLString))
                .argument(GraphQLArgument.newArgument().name("disponible").type(Scalars.GraphQLBoolean))
                .dataFetcher(env -> mutationResolver.updateLibro(
                    env.getArgument("id"),
                    env.getArgument("titulo"),
                    env.getArgument("autor"),
                    env.getArgument("isbn"),
                    env.getArgument("disponible"))))
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("deleteLibro")
                .type(Scalars.GraphQLBoolean)
                .argument(GraphQLArgument.newArgument().name("id").type(Scalars.GraphQLID))
                .dataFetcher(env -> mutationResolver.deleteLibro(env.getArgument("id"))))
            .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
            .query(queryType)
            .mutation(mutationType)
            .build();

        return GraphQL.newGraphQL(schema).build();
    }
}
