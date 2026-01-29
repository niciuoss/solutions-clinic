# Planejamento: Itens do menu por tipo de usuário

---

## Implementado (todas as etapas)

| Etapa                   | Status | Onde                                                                                   |
| ----------------------- | ------ | -------------------------------------------------------------------------------------- |
| 1 – Config da navegação | ✅     | `config/navigation.ts` (`NAV_ROUTES`, `getRouteRoles`, `isPathAllowedForRole`)         |
| 2 – Sidebar por role    | ✅     | `components/layout/Sidebar.tsx` (usa `NAV_ROUTES`, filtra por `user.role`)             |
| 3 – Proteção de rota    | ✅     | `app/(dashboard)/layout.tsx` (`useEffect` + `isPathAllowedForRole` → redirect + toast) |
| 4 – Fonte única         | ✅     | Matriz vive só em `config/navigation.ts`                                               |

**Checklist:** matriz definida; config em `config/navigation.ts`; proteção de rota no layout; tipo `UserRole` usado na config.

---

## 1. Objetivo

Controlar quais itens do menu lateral (Sidebar) cada tipo de usuário vê, de forma explícita e fácil de manter, e (opcionalmente) proteger rotas no frontend para quem acessar a URL diretamente.

---

## 2. Papéis (roles) no sistema

| Role (frontend)      | Descrição típica              |
| -------------------- | ----------------------------- |
| `ADMIN_CLINIC`       | Dono/administrador da clínica |
| `PROFISSIONAL_SAUDE` | Médico, dentista, fisio etc.  |
| `RECEPCIONISTA`      | Recepção/secretaria           |

---

## 3. Matriz sugerida: quem vê o quê

Proposta de visibilidade por item de menu. Ajuste conforme regra de negócio.

| Item do menu  | ADMIN_CLINIC | PROFISSIONAL_SAUDE | RECEPCIONISTA |
| ------------- | ------------ | ------------------ | ------------- |
| Dashboard     | ✅           | ✅                 | ✅            |
| Pacientes     | ✅           | ✅                 | ✅            |
| Agendamentos  | ✅           | ✅                 | ✅            |
| Prontuários   | ✅           | ✅                 | ✅            |
| Profissionais | ✅           | ❌ ou ✅\*         | ❌            |
| Procedimentos | ✅           | ✅\*               | ❌ ou ✅\*\*  |
| Usuários      | ✅           | ❌                 | ❌            |
| Salas         | ✅           | ❌                 | ❌            |
| Financeiro    | ✅           | ❌ ou ✅\*\*\*     | ❌            |
| Configurações | ✅           | ❌ ou ✅           | ❌            |

\* Profissional pode precisar ver “Profissionais” (lista) e “Procedimentos” (seus procedimentos).  
\*\* Recepcionista pode precisar de Procedimentos para agendamento.  
\*\*\* Se houver visão “minha comissão”, o profissional pode ver parte do Financeiro.

**Recomendação inicial (simples):**

- **ADMIN_CLINIC:** vê tudo.
- **PROFISSIONAL_SAUDE:** Dashboard, Pacientes, Agendamentos, Prontuários, Profissionais (só leitura/lista), Procedimentos, Configurações (ou só “meu perfil”).
- **RECEPCIONISTA:** Dashboard, Pacientes, Agendamentos, Prontuários, Procedimentos, Configurações (ou só “meu perfil”).

Definir a matriz final é decisão de negócio; o plano abaixo serve para qualquer combinação.

---

## 4. Abordagem técnica

### 4.1 Opção A – Roles no próprio array de navegação (recomendada)

- Cada item do menu tem um campo **`roles`** (array de `UserRole`) indicando **quem pode ver** o item.
- Se `roles` não existir, considerar “todos os roles” ou “apenas ADMIN_CLINIC”, conforme padrão escolhido.
- No Sidebar: para cada item, verificar `user.role` está em `item.roles` (e, se quiser, `user` não bloqueado etc.).

**Vantagens:** simples, tudo em um lugar (config da navegação), fácil adicionar/remover roles por item.

### 4.2 Opção B – Permissões nomeadas (ex.: `canManageUsers`)

- Definir permissões (ex.: `canManageUsers`, `canViewFinancial`) e mapear role → permissões.
- Cada item do menu fica atrelado a uma permissão.
- Sidebar filtra por “usuário tem a permissão X”.

**Vantagens:** escala melhor se no futuro tiver muitas telas e permissões; dá para alinhar com backend. **Desvantagem:** mais código e um mapeamento role → permissão para manter.

### 4.3 Recomendação

- **Fase 1:** Opção A (roles no array de navegação).
- Se depois surgir muitas regras ou integração com permissões do backend, evoluir para algo como a Opção B.

---

## 5. Plano de implementação (Opção A)

### Etapa 1 – Configuração da navegação

- **Onde:** um único módulo de config (ex.: `config/navigation.ts` ou dentro de `constants.ts`).
- **O quê:**
  - Definir tipo do item: `href`, `name`, `icon`, **`roles: UserRole[]`**.
  - Por convenção: se `roles` estiver vazio ou indefinido, tratar como “apenas ADMIN_CLINIC” (ou “todos”), conforme combinado.
- **Exemplo:**

```ts
// config/navigation.ts ou em constants
import { UserRole } from "@/types";

export const NAV_ROUTES = [
  {
    name: "Dashboard",
    href: ROUTES.DASHBOARD,
    icon: LayoutDashboard,
    roles: ["ADMIN_CLINIC", "PROFISSIONAL_SAUDE", "RECEPCIONISTA"],
  },
  {
    name: "Pacientes",
    href: ROUTES.PATIENTS,
    icon: Users,
    roles: ["ADMIN_CLINIC", "PROFISSIONAL_SAUDE", "RECEPCIONISTA"],
  },
  // ...
  {
    name: "Usuários",
    href: ROUTES.USERS,
    icon: UsersRound,
    roles: ["ADMIN_CLINIC"],
  },
  {
    name: "Financeiro",
    href: ROUTES.FINANCIAL,
    icon: DollarSign,
    roles: ["ADMIN_CLINIC"],
  },
  // ...
];
```

- Ajustar a matriz (quem vê o quê) apenas editando o array.

### Etapa 2 – Sidebar

- **Onde:** `components/layout/Sidebar.tsx`.
- **O quê:**
  - Importar a config de navegação (ex.: `NAV_ROUTES`).
  - Remover lógica baseada em `adminOnly`.
  - Para cada item: **mostrar apenas se `user?.role` estiver em `item.roles`** (e, se existir, checagem de `user` carregado).
- Garantir que, quando `user` ainda não carregou, não quebre (ex.: não mostrar itens ou mostrar só logo + “Carregando…”).

### Etapa 3 – Proteção de rota no frontend (opcional)

- **Problema:** usuário pode digitar `/users` ou `/financial` e cair na página mesmo sem ver o item no menu.
- **Onde:** layout do dashboard (`app/(dashboard)/layout.tsx`) ou um HOC/componente “RoleGuard” que envolve páginas sensíveis.
- **O quê:**
  - Ter uma lista (ou derivar da mesma config) de **rotas por role** (quem pode acessar qual path).
  - Se `pathname` for uma rota restrita e `user.role` não estiver autorizado: redirecionar para `/dashboard` (ou 403) e, se quiser, toast “Sem permissão”.
- **Alternativa:** um arquivo `config/routePermissions.ts` que lista `path → roles[]` e é usado no layout e no Sidebar.

### Etapa 4 – Consistência e manutenção

- **Única fonte da verdade:** idealmente, a matriz “quem vê o quê” vive só no array de navegação (e, se existir, em `routePermissions` derivado dele ou igual).
- **Documentar:** manter este `.md` atualizado quando mudar regras (ex.: “Profissional passou a ver Financeiro – apenas comissões”).

---

## 6. Checklist antes de codar

- [x] Definir matriz final (quem vê cada item) com produto/negócio.
- [x] Decidir onde a config vai ficar: `config/navigation.ts` (implementado).
- [x] Decidir se rotas protegidas no frontend serão feitas agora (Etapa 3) — sim, implementado no layout.
- [x] Garantir que o tipo `UserRole` está importado e usado na config para evitar typos.

---

## 7. Resumo

| Etapa | Ação                                                                             |
| ----- | -------------------------------------------------------------------------------- |
| 1     | Criar config de navegação com `roles: UserRole[]` por item.                      |
| 2     | No Sidebar, filtrar itens por `user.role` em `item.roles` e remover `adminOnly`. |
| 3     | (Opcional) No layout ou guard, bloquear acesso a rotas por role e redirecionar.  |
| 4     | Documentar e manter uma única fonte para “quem vê o quê”.                        |

Com isso, a manipulação dos itens do menu por tipo de usuário fica explícita, centralizada e fácil de alterar quando as regras de permissão mudarem.
