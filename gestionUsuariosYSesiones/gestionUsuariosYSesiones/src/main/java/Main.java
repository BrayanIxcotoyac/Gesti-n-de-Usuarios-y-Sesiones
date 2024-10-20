import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static SesionDAO sesionDAO = new SesionDAO();
    private static JPanel panelPrincipal;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pantalla Principal");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnInsertarUsuarioYSesion = new JButton("Insertar Usuario y Sesión");
        JButton btnMostrarUsuarios = new JButton("Mostrar Usuarios");
        JButton btnMostrarSesiones = new JButton("Mostrar Sesiones");
        JButton btnSalir = new JButton("Salir");

        btnInsertarUsuarioYSesion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMostrarUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMostrarSesiones.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelPrincipal.add(btnInsertarUsuarioYSesion);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(btnMostrarUsuarios);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(btnMostrarSesiones);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(btnSalir);

        frame.add(panelPrincipal);

        // Botones de acción
        btnInsertarUsuarioYSesion.addActionListener(e -> mostrarPanelInsertarUsuario());
        btnMostrarUsuarios.addActionListener(e -> mostrarUsuarios());
        btnMostrarSesiones.addActionListener(e -> mostrarSesiones());
        btnSalir.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Sesión Finalizada.");
            HibernateUtil.shutdown();
            System.exit(0);
        });

        frame.setVisible(true);
    }

    private static void mostrarPanelInsertarUsuario() {
        panelPrincipal.removeAll();

        JTextField nombreField = new JTextField(20);
        JTextField correoField = new JTextField(20);
        LocalDate fechaActual = LocalDate.now();
        String fechaFormateada = fechaActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        JTextField fechaField = new JTextField(fechaFormateada);
        fechaField.setEditable(false);
        JButton btnGuardar = new JButton("Guardar");

        nombreField.setMaximumSize(new Dimension(700, nombreField.getPreferredSize().height));
        correoField.setMaximumSize(new Dimension(700, correoField.getPreferredSize().height));
        fechaField.setMaximumSize(new Dimension(700, fechaField.getPreferredSize().height));

        panelPrincipal.add(new JLabel("Nombre:"));
        panelPrincipal.add(nombreField);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(new JLabel("Correo:"));
        panelPrincipal.add(correoField);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(new JLabel("Fecha:"));
        panelPrincipal.add(fechaField);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(btnGuardar);

        panelPrincipal.revalidate();
        panelPrincipal.repaint();

        btnGuardar.addActionListener(ev -> {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            if (!nombre.isEmpty() && !correo.isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setCorreo(correo);
                Sesion sesion = new Sesion();
                sesion.setFechaInicioSesion(LocalDateTime.now());
                sesion.setUsuario(usuario);
                usuario.getSesiones().add(sesion);
                usuarioDAO.insertarUsuario(usuario);
                sesionDAO.insertarSesion(sesion);
                JOptionPane.showMessageDialog(panelPrincipal, "Usuario y sesión insertados exitosamente.");
            } else {
                JOptionPane.showMessageDialog(panelPrincipal, "Todos los campos son obligatorios.");
            }
        });
    }

    private static void mostrarUsuarios() {
        panelPrincipal.removeAll();

        List<Usuario> usuarios = usuarioDAO.obtenerUsuarios();
        String[] columnNames = {"ID", "Nombre", "Correo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Usuario usuario : usuarios) {
            Object[] row = {usuario.getId(), usuario.getNombre(), usuario.getCorreo()};
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Usuarios"));
        panelPrincipal.add(scrollPane);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    private static void mostrarSesiones() {
        panelPrincipal.removeAll();

        List<Sesion> sesiones = sesionDAO.obtenerSesiones();
        String[] columnNames = {"ID Sesión", "ID Usuario", "Fecha Inicio"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Sesion sesion : sesiones) {
            Object[] row = {sesion.getId(), sesion.getUsuario().getId(), sesion.getFechaInicioSesion()};
            model.addRow(row);
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Sesiones"));

        panelPrincipal.add(scrollPane);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }
}
