'use client'

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/hooks/useAuth';
import { createProfessionalAction, updateProfessionalAction, addProfessionalToClinicAction } from '@/actions/professional-actions';
import { getProfessionalsAction } from '@/actions/user-actions';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from 'sonner';
import { Loader2, GraduationCap, FileText, User } from 'lucide-react';
import { useState, useEffect } from 'react';
import type { Professional, CreateProfessionalRequest } from '@/types';
import { DocumentType } from '@/types/professional.types';
import { professionalSchema } from '@/lib/validators';
import type { User } from '@/types';

type ProfessionalFormData = z.infer<typeof professionalSchema>;

interface ProfessionalFormProps {
  professional?: Professional;
  onSuccess?: () => void;
}

export function ProfessionalForm({ professional, onSuccess }: ProfessionalFormProps) {
  const router = useRouter();
  const { user } = useAuth();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoadingUsers, setIsLoadingUsers] = useState(false);

  const isEditing = !!professional;

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<ProfessionalFormData>({
    resolver: zodResolver(professionalSchema),
    defaultValues: {
      userId: professional?.user?.id || '',
      specialty: professional?.specialty || '',
      documentType: professional?.documentType || DocumentType.CRM,
      documentNumber: professional?.documentNumber || '',
      documentState: professional?.documentState,
      bio: professional?.bio,
    },
  });

  useEffect(() => {
    async function loadUsers() {
      if (!isEditing) {
        setIsLoadingUsers(true);
        try {
          const result = await getProfessionalsAction();
          if (result.success && result.data) {
            setUsers(result.data);
          }
        } catch (error) {
          console.error('Erro ao carregar usuários:', error);
        } finally {
          setIsLoadingUsers(false);
        }
      }
    }

    loadUsers();
  }, [isEditing]);

  const onSubmit = async (data: ProfessionalFormData) => {
    if (!user?.clinicId) {
      toast.error('Erro: Clínica não identificada. Faça login novamente.');
      return;
    }

    setIsSubmitting(true);

    try {
      let result;

      if (isEditing && professional) {
        // Atualizar profissional existente
        result = await updateProfessionalAction(professional.id, data);
      } else {
        // Criar novo profissional ou adicionar à clínica
        if (data.userId) {
          // Usar addProfessionalToClinic para adicionar profissional à clínica
          result = await addProfessionalToClinicAction(user.clinicId, {
            userId: data.userId,
            specialty: data.specialty,
            documentType: data.documentType,
            documentNumber: data.documentNumber,
            documentState: data.documentState,
            bio: data.bio,
          });
        } else {
          // Criar profissional usando createProfessional (requer userId e tenantId)
          result = await createProfessionalAction({
            userId: user.id,
            tenantId: user.clinicId!,
            specialty: data.specialty,
            documentType: data.documentType,
            documentNumber: data.documentNumber,
            documentState: data.documentState,
            bio: data.bio,
          });
        }
      }

      if (result.success) {
        toast.success(
          isEditing
            ? 'Profissional atualizado com sucesso!'
            : 'Profissional cadastrado com sucesso!'
        );
        if (onSuccess) {
          onSuccess();
        } else {
          router.push('/professionals');
        }
      } else {
        toast.error(result.error || 'Erro ao salvar profissional');
      }
    } catch (error) {
      toast.error('Erro inesperado ao salvar profissional');
      console.error(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Informações do Profissional */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-lg">
            <User className="h-5 w-5" />
            Informações do Profissional
          </CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          {!isEditing && (
            <div className="sm:col-span-2">
              <Label htmlFor="userId">Usuário *</Label>
              <Select
                value={watch('userId') || ''}
                onValueChange={(value) => setValue('userId', value)}
                disabled={isLoadingUsers}
              >
                <SelectTrigger className={errors.userId ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Selecione o usuário" />
                </SelectTrigger>
                <SelectContent>
                  {isLoadingUsers ? (
                    <div className="p-2 text-sm text-muted-foreground">
                      Carregando usuários...
                    </div>
                  ) : users.length > 0 ? (
                    users.map((userOption) => (
                      <SelectItem key={userOption.id} value={userOption.id}>
                        {userOption.fullName} - {userOption.email}
                      </SelectItem>
                    ))
                  ) : (
                    <div className="p-2 text-sm text-muted-foreground">
                      Nenhum usuário disponível
                    </div>
                  )}
                </SelectContent>
              </Select>
              {errors.userId && (
                <p className="mt-1 text-sm text-red-500">{errors.userId.message}</p>
              )}
            </div>
          )}

          {isEditing && (
            <div className="sm:col-span-2">
              <Label>Usuário</Label>
              <Input
                value={professional.user.fullName}
                disabled
                className="bg-muted"
              />
            </div>
          )}

          <div className="sm:col-span-2">
            <Label htmlFor="specialty">Especialidade *</Label>
            <Input
              id="specialty"
              placeholder="Ex: Cardiologia, Fisioterapia, Odontologia..."
              {...register('specialty')}
              className={errors.specialty ? 'border-red-500' : ''}
            />
            {errors.specialty && (
              <p className="mt-1 text-sm text-red-500">{errors.specialty.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Documentação Profissional */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-lg">
            <FileText className="h-5 w-5" />
            Documentação Profissional
          </CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <div>
            <Label htmlFor="documentType">Tipo de Documento *</Label>
            <Select
              value={watch('documentType') || ''}
              onValueChange={(value) => setValue('documentType', value as DocumentType)}
            >
              <SelectTrigger className={errors.documentType ? 'border-red-500' : ''}>
                <SelectValue placeholder="Selecione o tipo" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={DocumentType.CRM}>CRM - Conselho Regional de Medicina</SelectItem>
                <SelectItem value={DocumentType.CREFITO}>CREFITO - Conselho Regional de Fisioterapia</SelectItem>
                <SelectItem value={DocumentType.CRO}>CRO - Conselho Regional de Odontologia</SelectItem>
                <SelectItem value={DocumentType.CRP}>CRP - Conselho Regional de Psicologia</SelectItem>
                <SelectItem value={DocumentType.CRN}>CRN - Conselho Regional de Nutricionistas</SelectItem>
                <SelectItem value={DocumentType.COREN}>COREN - Conselho Regional de Enfermagem</SelectItem>
                <SelectItem value={DocumentType.OUTRO}>OUTRO</SelectItem>
              </SelectContent>
            </Select>
            {errors.documentType && (
              <p className="mt-1 text-sm text-red-500">{errors.documentType.message}</p>
            )}
          </div>

          <div>
            <Label htmlFor="documentNumber">Número do Documento *</Label>
            <Input
              id="documentNumber"
              placeholder="Ex: 123456"
              {...register('documentNumber')}
              className={errors.documentNumber ? 'border-red-500' : ''}
            />
            {errors.documentNumber && (
              <p className="mt-1 text-sm text-red-500">{errors.documentNumber.message}</p>
            )}
          </div>

          <div>
            <Label htmlFor="documentState">Estado do Documento (UF)</Label>
            <Select
              value={watch('documentState') || undefined}
              onValueChange={(value) => {
                setValue('documentState', value);
              }}
            >
              <SelectTrigger>
                <SelectValue placeholder="Selecione o estado (opcional)" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AC">AC</SelectItem>
                <SelectItem value="AL">AL</SelectItem>
                <SelectItem value="AP">AP</SelectItem>
                <SelectItem value="AM">AM</SelectItem>
                <SelectItem value="BA">BA</SelectItem>
                <SelectItem value="CE">CE</SelectItem>
                <SelectItem value="DF">DF</SelectItem>
                <SelectItem value="ES">ES</SelectItem>
                <SelectItem value="GO">GO</SelectItem>
                <SelectItem value="MA">MA</SelectItem>
                <SelectItem value="MT">MT</SelectItem>
                <SelectItem value="MS">MS</SelectItem>
                <SelectItem value="MG">MG</SelectItem>
                <SelectItem value="PA">PA</SelectItem>
                <SelectItem value="PB">PB</SelectItem>
                <SelectItem value="PR">PR</SelectItem>
                <SelectItem value="PE">PE</SelectItem>
                <SelectItem value="PI">PI</SelectItem>
                <SelectItem value="RJ">RJ</SelectItem>
                <SelectItem value="RN">RN</SelectItem>
                <SelectItem value="RS">RS</SelectItem>
                <SelectItem value="RO">RO</SelectItem>
                <SelectItem value="RR">RR</SelectItem>
                <SelectItem value="SC">SC</SelectItem>
                <SelectItem value="SP">SP</SelectItem>
                <SelectItem value="SE">SE</SelectItem>
                <SelectItem value="TO">TO</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Informações Adicionais */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-lg">
            <GraduationCap className="h-5 w-5" />
            Informações Adicionais
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div>
            <Label htmlFor="bio">Biografia</Label>
            <Textarea
              id="bio"
              placeholder="Descreva a formação e experiência profissional..."
              {...register('bio')}
              rows={6}
              className={errors.bio ? 'border-red-500' : ''}
            />
            {errors.bio && (
              <p className="mt-1 text-sm text-red-500">{errors.bio.message}</p>
            )}
            <p className="mt-1 text-sm text-muted-foreground">
              Esta informação será exibida no perfil do profissional.
            </p>
          </div>
        </CardContent>
      </Card>

      {/* Botões de Ação */}
      <div className="flex justify-end gap-4">
        <Button
          type="button"
          variant="outline"
          onClick={() => router.back()}
          disabled={isSubmitting}
        >
          Cancelar
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
          {isEditing ? 'Salvar Alterações' : 'Cadastrar Profissional'}
        </Button>
      </div>
    </form>
  );
}
