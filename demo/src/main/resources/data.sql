-- Datos de prueba para el sistema ERP
-- Se ejecuta automáticamente al iniciar la aplicación

-- Insertar roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrador del sistema'),
('USER', 'Usuario estándar')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuarios
INSERT INTO users (username, password, full_name, email, is_active) VALUES 
('admin', '$2a$10$N.wmKN0.5Z5qWzKGgGLb.eY8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8', 'Administrador', 'admin@empresa.com', true),
('usuario1', '$2a$10$N.wmKN0.5Z5qWzKGgGLb.eY8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8K8', 'Usuario Test', 'usuario1@empresa.com', true)
ON CONFLICT (username) DO NOTHING;

-- Insertar relación usuario-rol
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'usuario1' AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- Insertar clientes
INSERT INTO customers (name, email, phone, address) VALUES 
('Empresa ABC S.A.', 'contacto@empresaabc.com', '2234-5678', 'Zona 10, Ciudad de Guatemala'),
('Comercial XYZ', 'ventas@comercialxyz.com', '2345-6789', 'Zona 4, Mixco'),
('Distribuidora 123', 'info@dist123.com', '2456-7890', 'Zona 12, Villa Nueva'),
('Servicios Integrales', 'servicios@integrales.com', '2567-8901', 'Zona 1, Ciudad de Guatemala'),
('Tecnología Avanzada', 'tech@avanzada.com', '2678-9012', 'Zona 15, Ciudad de Guatemala')
ON CONFLICT DO NOTHING;

-- Insertar productos
INSERT INTO products (name, description, price, unit) VALUES 
('Laptop Dell Inspiron', 'Laptop Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD', 4500.00, 'UNIDAD'),
('Mouse Inalámbrico', 'Mouse inalámbrico óptico USB 2.4GHz con receptor nano', 75.50, 'UNIDAD'),
('Teclado Mecánico', 'Teclado mecánico RGB con switches Cherry MX Blue', 350.00, 'UNIDAD'),
('Monitor 24"', 'Monitor LED 24 pulgadas Full HD 1920x1080', 1200.00, 'UNIDAD'),
('Impresora Multifuncional', 'Impresora HP multifuncional con WiFi, scanner y copiadora', 850.00, 'UNIDAD'),
('Disco Duro Externo 1TB', 'Disco duro externo USB 3.0 de 1TB', 450.00, 'UNIDAD'),
('Webcam HD', 'Cámara web HD 1080p con micrófono integrado', 125.00, 'UNIDAD'),
('Parlantes Bluetooth', 'Parlantes estéreo Bluetooth 5.0 con batería recargable', 275.00, 'UNIDAD'),
('Cable HDMI', 'Cable HDMI 2.0 de 2 metros 4K Ultra HD', 45.00, 'UNIDAD'),
('Memoria USB 32GB', 'Memoria USB 3.0 de 32GB alta velocidad', 65.00, 'UNIDAD')
ON CONFLICT DO NOTHING;

-- Insertar proveedores
INSERT INTO suppliers (name, email, phone, address, contact_person) VALUES 
('Proveedor Tech GT', 'ventas@techgt.com', '2234-1111', 'Zona 9, Ciudad de Guatemala', 'Juan Pérez'),
('Distribuidora Nacional', 'compras@distnacional.com', '2345-2222', 'Zona 7, Mixco', 'María López'),
('Importaciones del Sur', 'importaciones@sur.com', '2456-3333', 'Zona 18, Ciudad de Guatemala', 'Carlos Rodríguez'),
('Electrónicos Modernos', 'electronicos@modernos.com', '2567-4444', 'Zona 11, Ciudad de Guatemala', 'Ana García'),
('Suministros Empresariales', 'suministros@empresariales.com', '2678-5555', 'Zona 4, Villa Nueva', 'Luis Martínez')
ON CONFLICT DO NOTHING;

-- Insertar cuentas contables
INSERT INTO accounts (code, name, account_type, parent_id) VALUES 
('1', 'ACTIVOS', 'ASSET', NULL),
('1.1', 'ACTIVOS CORRIENTES', 'ASSET', (SELECT id FROM accounts WHERE code = '1')),
('1.1.1', 'CAJA Y BANCOS', 'ASSET', (SELECT id FROM accounts WHERE code = '1.1')),
('1.1.2', 'CUENTAS POR COBRAR', 'ASSET', (SELECT id FROM accounts WHERE code = '1.1')),
('1.1.3', 'INVENTARIOS', 'ASSET', (SELECT id FROM accounts WHERE code = '1.1')),
('2', 'PASIVOS', 'LIABILITY', NULL),
('2.1', 'PASIVOS CORRIENTES', 'LIABILITY', (SELECT id FROM accounts WHERE code = '2')),
('2.1.1', 'CUENTAS POR PAGAR', 'LIABILITY', (SELECT id FROM accounts WHERE code = '2.1')),
('3', 'PATRIMONIO', 'EQUITY', NULL),
('3.1', 'CAPITAL', 'EQUITY', (SELECT id FROM accounts WHERE code = '3')),
('4', 'INGRESOS', 'INCOME', NULL),
('4.1', 'VENTAS', 'INCOME', (SELECT id FROM accounts WHERE code = '4')),
('5', 'GASTOS', 'EXPENSE', NULL),
('5.1', 'COSTO DE VENTAS', 'EXPENSE', (SELECT id FROM accounts WHERE code = '5')),
('5.2', 'GASTOS ADMINISTRATIVOS', 'EXPENSE', (SELECT id FROM accounts WHERE code = '5'))
ON CONFLICT (code) DO NOTHING;

-- Insertar bancos
INSERT INTO banks (name, address, phone, contact_person) VALUES 
('Banco Industrial', 'Torre Bancaria, Zona 10', '2420-3000', 'Gerente Comercial'),
('Banco G&T Continental', 'Edificio G&T, Zona 9', '2338-6800', 'Ejecutivo de Cuentas'),
('Banrural', 'Torre Banrural, Zona 4', '2418-8888', 'Asesor Empresarial'),
('Banco de Desarrollo Rural', 'Edificio Bancafé, Zona 1', '2230-2000', 'Gerente Regional'),
('Banco Agromercantil', 'Torre BAM, Zona 10', '2338-2400', 'Director Comercial')
ON CONFLICT DO NOTHING;

-- Insertar cajas registradoras
INSERT INTO cash_registers (name, location, current_balance, is_active) VALUES 
('Caja Principal', 'Recepción - Planta Baja', 1500.00, true),
('Caja Secundaria', 'Oficina Administrativa - Segundo Piso', 500.00, true),
('Caja de Emergencia', 'Bodega - Planta Baja', 200.00, false),
('Caja Móvil', 'Vehículo de Ventas', 300.00, true),
('Caja Eventos', 'Sala de Conferencias', 0.00, false)
ON CONFLICT DO NOTHING;