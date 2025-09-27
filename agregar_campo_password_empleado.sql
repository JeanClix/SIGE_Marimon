-- Script SQL para agregar el campo password a la tabla Empleado
-- Ejecutar en el SQL Editor de Supabase

-- Agregar el campo password a la tabla Empleado
ALTER TABLE "Empleado" 
ADD COLUMN "password" TEXT;

-- Agregar comentario al campo
COMMENT ON COLUMN "Empleado"."password" IS 'Contraseña del empleado para login';

-- Opcional: Crear un índice para búsquedas por email (si no existe)
CREATE INDEX IF NOT EXISTS "idx_empleado_email" ON "Empleado" ("email_corporativo");

-- Verificar que el campo se agregó correctamente
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'Empleado' 
ORDER BY ordinal_position;
