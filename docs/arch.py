from graphviz import Digraph

dot = Digraph(comment='Arquitectura de la Aplicación')

# Nodo Cliente
dot.node('A', 'Cliente\n(Web Browser/App)')

# Nodo Frontend
dot.node('B', 'Frontend\n(Angular)\n- Login\n- Perfil\n- Publicaciones y Likes')

# Nodo API Gateway (Opcional)
dot.node('C', 'API Gateway\n(JWT)')

# Nodos de Microservicios del Backend
dot.node('D', 'Microservicio de Usuarios\n(CRUD y Autenticación)')
dot.node('E', 'Microservicio de Publicaciones\n(Creación y Likes)')

# Nodo de Base de Datos
dot.node('F', 'PostgreSQL\n(Base de Datos)')

# Nodo de Contenedores
dot.node('G', 'Docker Compose\n(Orquestación de Contenedores)')

# Relaciones
dot.edge('A', 'B', 'Acceso a la app')
dot.edge('B', 'C', 'Envía peticiones (JWT)')
dot.edge('C', 'D', 'Ruteo de solicitudes')
dot.edge('C', 'E', 'Ruteo de solicitudes')
dot.edge('D', 'F', 'Operaciones CRUD')
dot.edge('E', 'F', 'Gestión de publicaciones y likes')
dot.edge('G', 'D', 'Contenedor')
dot.edge('G', 'E', 'Contenedor')
dot.edge('G', 'F', 'Contenedor')

# Visualización
dot.render('arquitectura_aplicacion', format='png', view=False)
dot_source = dot.source
dot_source  # Se muestra el código fuente del diagrama

