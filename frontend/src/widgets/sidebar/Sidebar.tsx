import CategoryOutlinedIcon from '@mui/icons-material/CategoryOutlined';
import LocalOfferOutlinedIcon from '@mui/icons-material/LocalOfferOutlined';
import MenuBookOutlinedIcon from '@mui/icons-material/MenuBookOutlined';
import PeopleOutlineOutlinedIcon from '@mui/icons-material/PeopleOutlineOutlined';
import RestaurantOutlinedIcon from '@mui/icons-material/RestaurantOutlined';
import StraightenOutlinedIcon from '@mui/icons-material/StraightenOutlined';
import { Typography } from '@mui/material';
import { NavLink } from 'react-router-dom';

import styles from '@/widgets/sidebar/Sidebar.module.css';

const navigation = [
  { to: '/recipes', label: 'Рецепты', icon: <MenuBookOutlinedIcon fontSize="small" /> },
  { to: '/ingredients', label: 'Ингредиенты', icon: <RestaurantOutlinedIcon fontSize="small" /> },
  { to: '/categories', label: 'Категории', icon: <CategoryOutlinedIcon fontSize="small" /> },
  { to: '/tags', label: 'Теги', icon: <LocalOfferOutlinedIcon fontSize="small" /> },
  { to: '/units', label: 'Единицы', icon: <StraightenOutlinedIcon fontSize="small" /> },
  { to: '/users', label: 'Пользователи', icon: <PeopleOutlineOutlinedIcon fontSize="small" /> },
];

export const Sidebar = () => {
  return (
    <aside className={styles.sidebar}>
      <div className={styles.brand}>
        <Typography variant="h5">Recipe Book</Typography>
      </div>
      <nav className={styles.nav}>
        {navigation.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `${styles.link} ${isActive ? styles.active : ''}`.trim()}
          >
            {item.icon}
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};
